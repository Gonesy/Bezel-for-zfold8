# Bezel design verification — 2026-09-12

## Current revision

The latest user comparisons supersede earlier visual acceptance notes. Previous
captures do not validate the current renderer, and the previous claim that no
significant visual differences remained was not supported by the user's review.

Reference attachments inspected for this revision (under the supplied temporary attachment directory):

- 6617155661278991353 and 10144095972279868017: outer hinge length and left edge.
- 13462680082672810619: side key positions and lengths.
- 7090237154694976284 and 16680779777012068161: outer actual/app proportions.
- 972185632810095543 and 5268993257736709773: inner actual/app proportions.
- 6049099534648656404: inner center hinge bridge and protective lips.

## Implementation

- One height-based geometry model maintains the published screen aspect ratio and
  equal physical insets on all four sides for both screen modes.
- Outer hinge occupies its own space outside the main chassis and stops short of
  the top and bottom. It no longer paints over the left black surround.
- Side controls use explicit top/end coordinates instead of alignment bias.
- Metal rails and antenna breaks are thicker; the black surround has a narrow
  edge transition. Inner top/bottom have four breaks, both sides have two;
  outer top/bottom and right side each have two.
- Inner center uses a recessed rectangular bridge between raised protective lips.
- Metal is drawn in layered cross-sections with narrow highlights and dark recesses.
  Graphite uses a dark base rather than a broadly bright surface.

## Local validation

`./gradlew testDebugUnitTest lintDebug assembleDebug --console=plain`

Geometry tests check equal borders and exact screen ratio at multiple sizes,
outer hinge isolation/shortened ends, and key placement. These are structural
checks, not proof of a pixel-perfect visual match to the photographs.

No phone connection, device installation, instrumented tests, or new device
comparison captures were performed for this revision, as requested by the user.
Final visual acceptance remains pending the user's own installation and review.

## Follow-up after user device review

References: 9018156650513871095 (outer left edge actual/app comparison) and
12248707246237209919 (inner top surround).

- Outer chassis now has independent left/right corner radii, with a nearly square
  left metal corner and a small left display corner. The hinge has shaped end
  transitions that meet the chassis rather than a detached capsule outline.
- Removed the full-width rounded inner protective bars responsible for the
  extra-layer appearance. Only a small central recessed joint remains.
- Increased narrow metallic highlights, rebalanced the bevel faces and varied
  reflections along the perimeter while retaining Graphite's dark base.
- Local unit tests, Lint and debug assembly completed successfully. No device
  testing was performed; material fidelity still requires user visual review.

## Inner hinge support follow-up

Reference 9739529354442571663 shows the missing local support assembly. Added
paired metal shoulders with a center split, a recessed dark cradle and short
side supports. The lower assembly mirrors the upper assembly, with all parts
inside the existing surround. The accepted outer renderer is unchanged.
Local unit tests, Lint and debug assembly passed. No device test was performed.

## User-supplied PNG integration — 2026-09-13

The six original files from `/Users/gonesy/Downloads/frame` are copied unchanged
into `app/src/main/res/drawable-nodpi`. They replace the procedural frame renderer.
`FrameArtwork.kt` maps both screen modes and all three finishes to the resources.

- Cover artwork: 1800 × 2700; display rectangle [161, 171, 1654, 2529).
- Inner artwork: 3360 × 2600; display rectangle [137, 135, 3223, 2465).
- Bounds include the aperture antialiasing. Source image draws below the PNG;
  the original opaque frame masks corners and supplies camera/hinge details.
- Existing preview capture for export includes both layers. Export resolution
  remains the existing preview-capture resolution, not the source PNG resolution.
- Asset tests exercise six resources, actual PNG dimensions, alpha channel,
  transparent interiors, opaque surrounding boundaries, and screen aspect ratios.
- No device installation or live export test was performed, per user instruction.

## Native-resolution export and updated inner artwork — 2026-09-13

- Replaced all three inner PNGs with the latest files from Downloads/frame;
  canvas dimensions and measured transparent opening are unchanged.
- Supersedes the preview-resolution export limitation above: `MockupRenderer`
  now allocates an ARGB canvas at 1800 × 2700 (cover) or 3360 × 2600 (inner).
  It decodes the source image with orientation handling, applies centered crop
  into the measured opening, then overlays the original unscaled PNG.
- Optional background is rendered into that same fixed-size canvas; background
  off preserves transparency. Preview zoom/pan and viewport size do not affect export.
- Export runs off the main thread, blocks duplicate saves and releases temporary
  bitmaps. Gallery insertion fails cleanly if opening/writing the output fails.
- Geometry tests cover both output sizes and crop behavior. Actual Android bitmap
  compositing/gallery behavior is not device-tested, per user instruction.
