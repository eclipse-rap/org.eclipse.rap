#!/usr/bin/env python3
# Copyright (c) 2026 EclipseSource and others.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
# SPDX-License-Identifier: EPL-2.0

"""Audit EPL migration metadata and optionally built binary/source archives."""
import argparse
import hashlib
import io
from pathlib import Path
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

ROOT = Path(__file__).resolve().parents[3]
URL = 'https://www.eclipse.org/legal/epl-2.0'
TEXT_SHA256 = '209fe24bf55677bbf81c2b0481c1403201fab57b3b4c609971eba4ec8162b99c'
HTML_SHA256 = '928c4a6af7e9cf82589e560f98ffbb6ade7385b59fec8cb4ef36a6bb91cf7018'
OLD = re.compile(r'epl-v10|EPL-1[.]0|Eclipse Public License.{0,25}1[.]0|Eclipse Public License v1[.]', re.I)


def properties(text):
    """Read logical property lines, respecting Java properties continuations."""
    result = {}
    logical = ''
    for line in text.splitlines():
        if not logical and (not line.strip() or line.lstrip().startswith(('#', '!'))):
            continue
        logical += line.lstrip() if logical else line
        slashes = len(logical) - len(logical.rstrip('\\'))
        if slashes % 2:
            logical = logical[:-1]
            continue
        match = re.match(r'([^=:\s]+)\s*[=:]\s*(.*)', logical)
        if match:
            result[match[1]] = match[2]
        logical = ''
    return result


def normalize_license(text):
    return re.sub(r'\s+', ' ', text).strip()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--artifacts', action='store_true', help='Also audit built plug-in and feature JARs')
    parser.add_argument('--baseline-ref', help='Compare original copyright and credits against a Git revision')
    args = parser.parse_args()
    errors = []
    counts = {'files': 0, 'plugins': 0, 'features': 0, 'archives': 0}

    def require(condition, message):
        if not condition:
            errors.append(message)

    names = subprocess.check_output(['git', 'ls-files', '--cached', '--others', '--exclude-standard', '-z'], cwd=ROOT).decode().split('\0')
    for name in sorted(set(names)):
        file = ROOT / name
        if not name or not file.is_file():
            continue
        data = file.read_bytes()
        if b'\0' in data:
            continue
        counts['files'] += 1
        text = data.decode('latin1')
        # Preserve upstream third-party license documents verbatim.
        if '/about_files/' not in name:
            declared = text
            if name == 'bundles/org.eclipse.rap.rwt/about.html':
                declared = text.split('<h3>Third Party Content', 1)[0]
            elif name.endswith('EPL-2.0-MIGRATION.md') or file == Path(__file__).resolve():
                declared = ''
            require(not OLD.search(declared), f'Legacy project declaration: {name}')
            require('Eclipse Foundation Software User Agreement' not in declared, f'Obsolete SUA: {name}')
        if file.suffix in ('.java', '.js', '.properties', '.xml', '.xsl', '.exsd', '.prefs') and 'Eclipse Public License 2.0' in text and re.search(r'Copyright', text, re.I):
            require('SPDX-License-Identifier' in text, f'Missing SPDX identifier: {name}')
            require(not re.search(r'SPDX-License-Identifier:\s*EPL-2[.]0[.]', text), f'Punctuation after SPDX identifier: {name}')
            require(not re.search(r'which\s+accompanies', text), f'Outdated header wording: {name}')
        if name.endswith('/META-INF/MANIFEST.MF'):
            directory = file.parent.parent
            build = directory / 'build.properties'
            if not build.is_file():
                continue
            counts['plugins'] += 1
            require(f'Bundle-License: {URL}' in text, f'Bundle license metadata: {name}')
            about = directory / 'about.html'
            agreement = directory / 'about_files/epl-2.0.html'
            require(about.is_file() and 'about_files/epl-2.0.html' in about.read_text(encoding='latin1'), f'Local license link: {directory.relative_to(ROOT)}')
            require(agreement.is_file() and hashlib.sha256(agreement.read_bytes()).hexdigest() == HTML_SHA256, f'License text: {agreement.relative_to(ROOT)}')
            props = properties(build.read_text(encoding='latin1'))
            for key in ('bin.includes', 'src.includes'):
                includes = [part.strip() for part in props.get(key, '').split(',')]
                require('about.html' in includes and 'about_files/' in includes, f'Legal file inclusion ({key}): {build.relative_to(ROOT)}')
        if name.startswith('features/') and name.endswith('/feature.xml'):
            counts['features'] += 1
            feature = ET.fromstring(data)
            require(feature.get('license-feature') is None and feature.get('license-feature-version') is None, f'Shared SUA dependency: {name}')
            require(feature.find('license').get('url') == 'license.html' and normalize_license(feature.findtext('license', '')) == normalize_license((ROOT / 'LICENSE').read_text()), f'Complete feature installation license: {name}')
            build = file.with_name('build.properties')
            includes = properties(build.read_text(encoding='latin1')).get('bin.includes', '')
            require('epl-2.0.html' in includes and 'license.html' in includes, f'Feature license inclusion: {name}')
            agreement = file.with_name('epl-2.0.html')
            require(agreement.is_file() and hashlib.sha256(agreement.read_bytes()).hexdigest() == HTML_SHA256, f'Feature license text: {name}')
            props = properties(file.with_name('feature.properties').read_text(encoding='latin1'))
            license_html = file.with_name('license.html')
            require(license_html.is_file() and license_html.read_bytes() == agreement.read_bytes(), f'Feature license.html is not the EPL 2.0 agreement: {name}')
            copyright_text = props.get('copyright', '')
            require('Eclipse Public License 2.0' in copyright_text and 'SPDX-License-Identifier: EPL-2.0' in copyright_text, f'Feature copyright notice: {name}')

    require(hashlib.sha256((ROOT / 'LICENSE').read_bytes()).hexdigest() == TEXT_SHA256, 'Root LICENSE differs from the official text (with trailing spaces removed)')
    require((ROOT / 'releng/org.eclipse.rap.build/legal/epl-2.0.html').read_bytes() == (ROOT / 'features/org.eclipse.rap.feature/epl-2.0.html').read_bytes(), 'Update-site license text mismatch')
    parent = (ROOT / 'releng/org.eclipse.rap.build/pom.xml').read_text()
    require('license-repo' not in parent, 'Obsolete shared SUA repository')
    require((ROOT / 'releng/org.eclipse.rap.build/legal/notice.html').read_bytes() == (ROOT / 'releng/org.eclipse.rap.build/legal/epl-2.0.html').read_bytes(), 'Update-site notice is not the EPL 2.0 agreement')
    for name in ('pom.xml', 'releng/org.eclipse.rap.build/pom.xml', 'releng/org.eclipse.rap.examples.build/parent/parent/pom.xml'):
        pom = ET.parse(ROOT / name)
        ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
        require(pom.findtext('m:licenses/m:license/m:url', namespaces=ns) == URL, f'Maven license metadata: {name}')
    import json
    require(json.loads((ROOT / 'package.json').read_text())['license'] == 'EPL-2.0', 'NPM license metadata')

    if args.baseline_ref:
        revision = subprocess.check_output(['git', 'rev-parse', '--verify', '--end-of-options', args.baseline_ref + '^{commit}'], cwd=ROOT).decode().strip()
        baseline = zipfile.ZipFile(io.BytesIO(subprocess.check_output(['git', 'archive', '--format=zip', revision], cwd=ROOT)))
        credit_pattern = re.compile(r'Contributors:\r?\n(.*?)(?:\*/|-->|^[#*]{5,})', re.M | re.S)
        preserved = 0
        for name in baseline.namelist():
            if name.endswith('/') or name == 'LICENSE' or name.endswith('/epl-v10.html') or name == 'releng/org.eclipse.rap.build/legal/notice.html':
                continue
            original = baseline.read(name)
            file = ROOT / name
            require(file.is_file(), f'Missing original file: {name}')
            if not file.is_file() or b'\0' in original:
                continue
            current = file.read_bytes()
            if '/about_files/' in name:
                require(original == current, f'Original third-party terms changed: {name}')
            before = original.decode('latin1').replace('\r\n', '\n')
            after = current.decode('latin1').replace('\r\n', '\n')
            notices = [line for line in before.splitlines() if re.search(r'copyright\s*(?:\(c\)|&copy;|\xa9)', line, re.I)]
            if name.endswith('.prefs'):
                notices = re.findall(r'Copyright[^\\<\r\n]+', before)
            for notice in notices:
                require(notice in after, f'Original copyright changed: {name}')
                preserved += 1
            for credit in credit_pattern.findall(before):
                require(credit in after, f'Original contributor section changed: {name}')
            for line in before.splitlines():
                if re.search(r'ongoing development|bugfix|bug.?fixing', line, re.I):
                    require(line in after.splitlines(), f'Original ongoing-development/bugfix credit changed: {name}')
        print(f'Original copyright notices preserved: {preserved}')

    if args.artifacts:
        for base in ('bundles', 'examples', 'tests', 'releng/org.eclipse.rap.clientbuilder', 'features'):
            for file in (ROOT / base).glob('**/target/*.jar'):
                if '-tests' in file.name:
                    continue
                with zipfile.ZipFile(file) as archive:
                    names = archive.namelist()
                    is_feature = 'feature.xml' in names
                    manifest = archive.read('META-INF/MANIFEST.MF').decode() if 'META-INF/MANIFEST.MF' in names else ''
                    is_plugin = 'Bundle-SymbolicName:' in manifest
                    if not is_feature and not is_plugin:
                        continue
                    counts['archives'] += 1
                    license_name = 'epl-2.0.html' if is_feature else 'about_files/epl-2.0.html'
                    require(license_name in names, f'Missing packaged license: {file.relative_to(ROOT)}')
                    if license_name in names:
                        require(b'Eclipse Public License - v 2.0' in archive.read(license_name), f'Incorrect packaged agreement: {file.relative_to(ROOT)}')
                    if is_feature:
                        require('license.html' in names, f'Missing packaged EPL 2.0 license.html: {file.relative_to(ROOT)}')
                        if 'license.html' in names:
                            require(hashlib.sha256(archive.read('license.html')).hexdigest() == HTML_SHA256, f'Packaged license.html is not EPL 2.0: {file.relative_to(ROOT)}')
                        packaged = ET.fromstring(archive.read('feature.xml'))
                        require(packaged.get('license-feature') is None, f'Packaged shared SUA dependency: {file.relative_to(ROOT)}')
                        require(normalize_license(packaged.findtext('license', '')) == normalize_license((ROOT / 'LICENSE').read_text()), f'Packaged install-time license is incomplete: {file.relative_to(ROOT)}')
        for repository in ('repository', 'repository.e4'):
            directory = ROOT / 'releng/org.eclipse.rap.build' / repository / 'target/repository'
            require(directory.is_dir(), f'Assembled update site missing: {repository}')
            if not directory.is_dir():
                continue
            for name in ('epl-2.0.html', 'notice.html'):
                file = directory / name
                require(file.is_file() and hashlib.sha256(file.read_bytes()).hexdigest() == HTML_SHA256, f'Update-site agreement: {repository}/{name}')
            content = directory / 'content.jar'
            if content.is_file():
                with zipfile.ZipFile(content) as archive:
                    metadata = ET.fromstring(archive.read('content.xml'))
            else:
                content = directory / 'content.xml'
                require(content.is_file(), f'Update-site metadata missing: {repository}')
                if not content.is_file():
                    continue
                metadata = ET.parse(content).getroot()
            units = [unit for unit in metadata.findall('./units/unit') if unit.get('id', '').startswith('org.eclipse.rap.') and unit.get('id', '').endswith('.feature.group')]
            require(len(units) == 4, f'Expected four RAP feature groups in {repository}')
            for unit in units:
                licenses = unit.findall('./licenses/license')
                require(len(licenses) == 1 and normalize_license(licenses[0].text or '') == normalize_license((ROOT / 'LICENSE').read_text()), f'Update-site installation license: {repository}/{unit.get("id")}')
        require(counts['archives'] > 0, 'No built plug-in or feature archives found')
    print(', '.join(f'{key}: {value}' for key, value in counts.items()))
    for error in errors:
        print(f'ERROR: {error}', file=sys.stderr)
    print('PASS' if not errors else f'FAIL: {len(errors)} issue(s)')
    return bool(errors)


if __name__ == '__main__':
    sys.exit(main())
