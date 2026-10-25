# AND101 Project 6 - CYOAPI Part 2: RecyclerView Edition

Submitted by: **Jorge Muñoz**

Time spent: **7** hours spent in total

## Summary

**MoonDay** is an android app that **queries the FreeAstroAPI for any selected date in the year, displays a horizontal RecyclerView of the weekly moon phases, and shows interactive tabs for lunar visuals, astronomical metrics, tropical zodiac signs, spiritual guidance, and upcoming phase forecasts.**

If I had to describe this project in three (3) emojis, they would be: **🌕 🔮 ✨**

## Application Features

The following REQUIRED features are completed:

- [x] Make an API call to an API of your choice using AsyncHTTPClient
- [x] Implement a RecyclerView to display a list of entries from the API
- [x] Display at least three (3) pieces of data for each RecyclerView item (1. Formatted Date, 2. Moon Phase SVG, 3. Phase Name & Illumination %)

The following STRETCH features are implemented:

- [x] Add a UI element for the user to interact with API further (Material 3 DatePicker Calendar)
- [ ] Show a `Toast` or `Snackbar` when an item is clicked
- [ ] Add item dividers with `DividerItemDecoration`

The following EXTRA features are implemented:

- [x] **Horizontal Weekly RecyclerView**: Sliding list showing moon phases for the week of the chosen date with infinite looping and smooth page synchronization.
- [x] **Zero Rate Limiting & Offline Astronomical Engine**: Seamless local calculation fallback so rate limits (`429`) or quota exhaustion never break the app.
- [x] **Top-Left Back Navigation**: Functional Back Arrow button in the top left corner of the header.
- [x] **Cosmic Starry Header**: Custom starry night background banner with shimmering star particles.
- [x] **Bubbly Fredoka Typography**: Enlarged Google Fredoka font for titles, cards, and buttons.
- [x] **Tabbed Data Navigation Bar**: Organized the rich API data into 4 clean tabs (`Phase`, `Astronomy`, `Guidance`, `Forecast`).

## Video Demo

Here's a video / GIF that demos all of the app's implemented features:

![Video Demo](Peek%202026-10-25%2002-24.gif)

GIF created with **Peek**

## Notes

During the creation of this app, I learned how to implement a horizontal RecyclerView with custom ViewHolders using AsyncHTTPClient, load SVG byte arrays dynamically into Compose & RecyclerView items, prevent API rate limiting with local astronomical calculations, configure top-left back navigation, and design cosmic starry headers.

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
