# Yfiton

[![CircleCI](https://circleci.com/gh/yfiton/yfiton.svg?style=shield)](https://circleci.com/gh/yfiton/yfiton) [![BrowserStack Status](https://www.browserstack.com/automate/badge.svg?badge_key=d083Q0V1akVRZWw5UncrOUp3eDhuUTZteGxRcXIrRzAzUk9xS1hWSC9Vaz0tLTlRYzFKNkxKdkcrUWdSRENmV1NuOEE9PQ==--6a2a3a83f1f6eeafd0b8a1fd96401cec789e6cb1)](https://www.browserstack.com/automate/public-build/d083Q0V1akVRZWw5UncrOUp3eDhuUTZteGxRcXIrRzAzUk9xS1hWSC9Vaz0tLTlRYzFKNkxKdkcrUWdSRENmV1NuOEE9PQ==--6a2a3a83f1f6eeafd0b8a1fd96401cec789e6cb1)

Yfiton is an API and command-line utility to send notifications using well-known or modern communication services.

## Installation

You can download one of the distributions from the [releases](https://github.com/yfiton/yfiton/releases/latest) section.

Once unpacked, the folder associated to the distribution is referred to as `YFITON_HOME`.

For running Yfiton, add `YFITON_HOME/bin` to your `PATH` environment variable.

Then, to check if Yfiton is properly installed, type `yfiton --version`.

## Basic usage

Triggering beep(s) using default speaker:
```
$ yfiton -n beep
$ yfiton -n beep -Ppattern="*** ** *"
```

Displaying rich desktop notification(s):
```
$ yfiton -n desktop -Pmessage="Lunch time!"
$ yfiton -n desktop -Pmessage="Lunch time!" -PhideAfter=10 -Pposition=TOP_RIGHT
```

Sending an email:
```
$ yfiton -n email -Phost=smtp.free.fr -Pfrom=username@free.fr -Pto=user@company.com \
    -Psubject="Build failure!" -Pbody="Build #42 has failed!" -Pusername=username@free.fr
```

Publishing a message on [Facebook](https://www.facebook.com):
```
$ yfiton -n facebook -Pmessage="I have bougth the new Xbox Elite controller. Incredible!"
$ yfiton -n facebook -Pmessage="My new puzzle!" -Pphoto=path/to/your/photo.jpg
```

Pushing a notification with [Pushbullet](https://www.pushbullet.com):
```
$ yfiton -n pushbullet -Ptitle="Weather alert!" -Pbody="Hurricane approaches"
$ yfiton -n pushbullet -Pbody="Please find report" -Pfile=path/to/file.extension
$ yfiton -n pushbullet -Pbody="New daily deal" -Purl="https://www.groupon.com"
```

Sending notification to [Slack](https://slack.com):
```
$ yfiton -n slack -Pmessage="Quick design session at 2pm"
$ yfiton -n slack -Pmessage="1 2 3 soleil!" -Pchannel=#random
```

Updating [Twitter](https://twitter.com) status:
```
$ yfiton -n twitter -Pstatus="5 Tips for Growing Your Business http://bit.ly/1PjsefI #marketing"
```
## Advanced usage

Yfiton is provided with several _notifiers_ that allow sending notifications using well-known or modern communication services. 
You can list available _notifiers_ as follows:

    $ yfiton --list-notifiers

A notifier is identified by a unique name. For instance, Facebook notifier has unique identifier `facebook`. It is possible to describe available parameters for Facebook notifier as below:

    $ yfiton --describe-notifier facebook

Most of the notifiers require to connect to a third-party service. Authentication parameters are stored by default in `$HOME/.yfiton`.

## License

Yfiton is released under Apache Software Foundation License v2.0. See LICENSE file included for more details.

# Acknowledgements

[![](https://user-images.githubusercontent.com/128898/42722048-9087dd44-8745-11e8-88e3-4c06833b7454.png)](https://browserstack.com)
