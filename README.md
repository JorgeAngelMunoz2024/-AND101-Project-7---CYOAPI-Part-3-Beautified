# AND101 Project 7 - CYOAPI Part 3: Beautified

Submitted by: **Jorge Muñoz**

Time spent: **3** hours spent in total

## Summary

**MoonDay** is an android app that **queries the FreeAstroAPI for any selected date in the year, displays a horizontal RecyclerView of the weekly moon phases, and showcases customized cosmic themes, styles, multiple downloadable typography fonts, and dark mode adaptive drawables across interactive tabs.**

If I had to describe this project in three (3) emojis, they would be: **🌕 🔮 ✨**

## Application Features

The following REQUIRED features are completed:

- [x] App contains a RecyclerView that displays a list of scrollable data
- [x] App displays at least two (2) pieces of data for each RecyclerView item (1. Formatted Date, 2. Moon Phase SVG, 3. Phase Name &amp; Illumination %)
- [x] Use a downloadable font with custom color and size (Google Fonts Cinzel &amp; Fredoka typography)
- [x] Modify the theme of the app in `themes.xml` (Cosmic Day &amp; Night mode theme palette, window background, status bar)
- [x] Define and apply at least one style in **either** `themes.xml` or a new file `styles.xml` (`Widget.MoonDay.TitleText`, `Widget.MoonDay.SubtitleText`, `Widget.MoonDay.Card`, `Widget.MoonDay.CardSelected`, `Widget.MoonDay.Button`)

The following STRETCH features are implemented:

- [x] Update the night theme to use different versions of styles when in dark mode (Adaptive `themes.xml` &amp; `values-night/styles.xml` color schemes and card borders)
- [x] Use different drawables when in dark mode (Night mode adaptive vector drawables and card shape drawables in `res/drawable-night/`)

The following EXTRA features are implemented:

- [x] **Cosmic Starry Header**: Custom starry night background banner with shimmering star particles.
- [x] **Multi-Font Aesthetic Typography**: Downloadable `Cinzel` font for titles paired with `Fredoka` for cards and badges.

## Video Demo

Here's a video / GIF that demos all of the app's implemented features:

![Video Demo](Peek%202026-11-18%2011-13.gif)

GIF created with **Peek**

## Notes

During the creation of this app, I learned how to integrate Google Fonts downloadable font providers in XML, configure adaptive Day/Night themes in `themes.xml`, define custom component styles in `styles.xml`, and create dark-mode specific drawables in `res/drawable-night/`.

## License

Copyright **2026** **Jorge Muñoz**

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
