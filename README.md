SFXR-Plus-Plus
==============

SFXR++/SFXR-Plus-plus isJava port of SFXR engine, with new features.
It's made to generate on-the-fly sounds from given parameters or given files during program execution.

But what is SFXR?
Quote from DrPepper:
> [SFXR] is a little tool I made in connection with the 10th Ludum Dare competition held in December 2007. Its original purpose was to provide a simple means of getting basic sound effects into a game for those people who were working hard to get their entries done within the 48 hours and didn't have time to spend looking for suitable ways of doing this. The idea was that they could just hit a few buttons in this application and get some largely randomized effects that were custom in the sense that the user could accept/reject each proposed sound.

An editor, SFXR++ Soundbox, is included in the library. It's developped in the same time as the library to assure a good work and give an easy tool for developpers. It's function is basically the same as SFXR ot BFXR, but will include a save/load function for it's sound type. At this moment, the Soundbox don't have any I/O functions implemented, but it's only question of time.

Features
--------

- Full SFXR port. Can generate sounds samples on-the-fly.
- Can play generated samples any time.
- Can store sound parameters in a very tiny file, and read it to regenerate the sound.
- BFXR's waveforms added!
- Include a utility to create SFXR++ sound files.
- Made to be used in game! Don't use sound files anymore for FXes! Generate them once and play them directly.
- The engine can export any sound to a .WAV file.
- Free, Open Source and under a free license. Do what you want from the source (altough SFXR++ Soundbox needs MiGlayout, and so needs an indicationm of MiGlayout's license).

Soundbox
--------

- Play with a fully functionnal soundbox integrated with SFXR++!
- Save/Load any sound!
- Uses the whole set of SFXR++ options!

To Do
-----

- Implement original SFXR's filetype I/O functions.

License
-------

[SFXR][sfxr] is a sound generator done by DrPepper, and is located here : http://www.drpetter.se/project_sfxr.html . It's under [MIT license][mitlicense].

[BFXR][bfxr] is a sound generator based on SFXR done by Increpare, and is located here : http://www.bfxr.net . It's under [Apache License 2.0][apachelicense].

[MiGLayout][miglayout] is a Swing/way layout manager done by MiG InfoCom AB, located here : http://www.miglayout.com . It's under [BSD License][bsdlicense].


[sfxr]: http://www.drpetter.se/project_sfxr.html
[bfxr]: http://www.bfxr.net
[miglayout]: http://www.miglayout.com

[mitlicense]: http://opensource.org/licenses/mit-license.php
[apachelicense]: http://www.apache.org/licenses/LICENSE-2.0.html
[bsdlicense]: http://www.migcalendar.com/miglayout/versions/4.0/license.txt