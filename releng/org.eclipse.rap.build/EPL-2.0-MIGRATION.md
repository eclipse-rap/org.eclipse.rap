# EPL 2.0 migration

RAP Runtime now declares EPL-2.0. Copyright years, ownership statements,
contributors, ongoing-development credits, and bugfix credits are retained.

## Official guidance and migration steps

1. Adopt the successor license using section 7 of the
   [EPL 1.0 agreement](https://www.eclipse.org/legal/epl-v10.html).
   This permits a contributor to distribute the program and its contributions
   under a newly published license version. The Eclipse Foundation's
   [migration explanation](https://www.eclipse.org/lists/locationtech-pmc/msg00748.html)
   describes updating headers and notices and communicating the change publicly.
   Sections 3.1 and 3.4 of the [official EPL 2.0 FAQ](https://www.eclipse.org/legal/epl-2.0/faq/)
   describe migration without a secondary license: update notices and headers
   and replace Software User Agreements with the complete EPL 2.0 agreement.
   The FAQ loads its text through JavaScript from
   [this official content file](https://www.eclipse.org/legal/documents/html/epl-2.0-faq.html).
2. Replace the agreement itself with the complete official
   [plain text](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt) and
   [HTML](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html) documents.
   Changing the title of the old agreement is insufficient.
3. Update license notices and SPDX identifiers, keeping copyright and
   attribution. The [Eclipse Project Handbook](https://www.eclipse.org/projects/handbook/#legaldoc)
   describes source notices, repository license and notice files, Maven metadata,
   plug-in about files, and feature legal documentation. Apply the same changes
   to editor templates, schema documentation, and displayed feature copyrights.
4. Ensure distributions contain the new agreement. Update feature includes,
   plug-in binary/source includes, and update-site legal files. Features supply
   the complete EPL 2.0 agreement in `license.html` and the `license` element
   in `feature.xml` shown during installation. No shared Software User Agreement is used.
5. Audit declarations, attribution, and built archives using the commands below.
   Announce the license change to downstream consumers and update external
   Eclipse project/release metadata before publishing the migrated release.

This is an EPL-2.0 migration without opting into a GPL secondary license.
[Section 3.2 and Exhibit A of EPL 2.0](https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html)
require a separate secondary-license notice; merely copying the agreement,
including its Exhibit A, does not enable secondary licensing. The archived
Foundation explanation distinguishes ordinary version migration from adding
secondary-license permissions.

## Changes in this repository

* `LICENSE` contains the official EPL 2.0 plain text. Only trailing spaces
  from the official download have been removed; the license wording is unchanged.
* Project headers, commented-out source, Eclipse JDT templates, extension
  schemas, README, NOTICE, and displayed feature notices now declare EPL-2.0.
  Header notices use the FAQ wording `which is available at`. Existing
  copyright years, owners, contributor credits, and `All rights reserved`
  statements are retained.
* Maven parent metadata and the root npm metadata declare EPL 2.0.
* All 60 plug-in/fragment projects with manifests and build properties include
  `about.html` and `about_files/epl-2.0.html` in binary and source packaging.
  Manifests declare the EPL 2.0 license URL. Existing third-party about sections
  and license documents are preserved.
* All eight features include the official EPL 2.0 HTML as both `epl-2.0.html`
  and `license.html`. Each `feature.xml` supplies the complete plain-text
  EPL 2.0 directly in its `license` element. The shared SUA feature
  dependency and its CBI repository have been removed.
* The update-site legal directory contains the official EPL 2.0 HTML in both
  `epl-2.0.html` and `notice.html`; the old SUA is replaced in place.

Intentional references to EPL 1.0 remain in the original qooxdoo third-party
licensing document and its third-party about link. These are upstream terms;
RAP's default license declarations and packaged project agreement are EPL 2.0.
Other third-party terms (including Apache, MIT, W3C, and LGPL notices) are retained.

## Verification

Run from the repository root:

```sh
python releng/org.eclipse.rap.build/scripts/verify-epl.py
mvn -B -DskipTests -Djgit.dirtyWorkingTree=warning clean package
python releng/org.eclipse.rap.build/scripts/verify-epl.py --artifacts
```

In PowerShell, quote the Maven `-D` arguments, particularly the dotted property:

```powershell
mvn -B '-DskipTests' '-Djgit.dirtyWorkingTree=warning' clean package
```

The warning setting permits packaging an uncommitted review checkout. It does
not change the license or require a commit. Packaging validation skips runtime
tests; source-preservation checks independently verify that Java/JavaScript
changes are confined to license notices.

The audit checks legacy project declarations, SPDX tags, exact agreement hashes,
complete install-time license text, Maven/npm/OSGi metadata, local about links, and
binary/source inclusion rules. `--artifacts` also inspects built plug-in, source,
and feature JARs for legal files and EPL 2.0 agreements. It does not claim to
verify external project metadata, other repositories, or previously released
artifacts. Earlier releases retain their original license.

Maintainers should coordinate the RAP Tools repository, project website, PMI
license metadata, and release announcement separately. Those external resources
are not modified by this repository change.

## Verification results for this migration

* Maven packaging succeeded for all 60 reactor modules, including both update sites.
  Runtime tests were skipped; test sources were compiled.
* The repository/packaging audit passed for 60 plug-in/fragment projects,
  eight features, and 106 built binary, source, and feature archives.
* Both assembled update sites contain EPL 2.0 legal files. All four RAP
  features in each site contain the complete EPL 2.0 in both HTML and
  installation metadata, with no shared SUA dependency.
* Comparison against the original Git revision preserved all 6,294 copyright
  notices and 5,998 contributor sections, including every original line
  mentioning ongoing development or bugfixing. All nine original third-party
  documents in `about_files` are unchanged.
* Independent diff inspection of 5,584 changed Java/JavaScript files found
  only legal-notice edits. Modified XML/schema/XSL files parsed successfully.
* Project notice edits pass diff whitespace checks with CRLF line endings allowed.
  Copied official HTML agreements and about templates retain upstream whitespace.

To repeat attribution preservation checks against the original revision:

```sh
python releng/org.eclipse.rap.build/scripts/verify-epl.py --artifacts --baseline-ref 0f02671e4^
```

`0f02671e4^` identifies the revision before the initial EPL 2.0 migration.
