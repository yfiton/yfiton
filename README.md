# Yfiton

[![Build Status](https://travis-ci.org/yfiton/yfiton.svg)](https://travis-ci.org/yfiton/yfiton)

Yfiton is an API and command-line utility to send notifications using well-known or modern communication services.

## Installation

You can download one of the distributions from [releases](https://github.com/yfiton/yfiton/releases/latest) section.

Once unpacked, the folder associated to the distribution is refered to as `YFITON_HOME`.

For running Yfiton, add `YFITON_HOME/bin` to your `PATH` environment variable.

Then, to check if Yfiton is properly installed just type `yfiton --version`.

## Basic usage

* Triggering beep(s) using default speaker:
  - `$> yfiton -n beep`
  - `$> yfiton -n beep -Ppattern="*** ** *"`
* Displaying rich desktop notification(s):
  - `$> yfiton -n desktop -Pmessage="Lunch time!"`
  - `$> yfiton -n desktop -Pmessage="Lunch time!" -PhideAfter=10 -Pposition=TOP_RIGHT`

## Advanced usage

Yfiton is provided with several _notifiers_ that allow to send notifications using well-known or modern communication services. 
You can list available _notifiers_ as follows:

    $> yfiton --list-notifiers

A notifier is identified by a unique name. For instance, Facebook notifier has unique identifier `facebook`. It is possible to describe available parameters for Facebook notifier as below:

    $> yfiton --describe-notifier facebook

Most of the notifiers require to connect to a third-party service. Authentication parameters are stored by default in `$HOME/.yfiton`.

## License

Yfiton is released under Apache Software Foundation License v2.0. See LICENSE file included for more details.
