# always_listener

A new Flutter project.

## Implementation

1) Implementing the Figma design - DONE
2) Establishing a channel between Flutter & Kotlin - DONE
    > This was done using method channel to trigger the periodic audio recorder as well as event channel
    > event channel is setup in flutter so that it can receive data from native
3) Setting up foreground service in Android (Kotlin) - DONE
4) Recording audio in Kotlin and sending it periodically to flutter - DONE

## Incomplete

1) I was not able to process the data received in flutter. Somehow the ByteStream received in flutter doesn't seem valid when trying to play it
2) Hence unable to send it to the API as well for transcription
