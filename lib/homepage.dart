import 'dart:typed_data';

import 'package:always_listener/audio_player.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
//=========Handle Deep Linking=========//
  var methodChannel = const MethodChannel('always_listener');

  Future<void> callNativeCode() async {
    try {
      var data = await methodChannel.invokeMethod('startForegroundService');
      print(data);
    } on PlatformException catch (e) {
      print(e);
    }
  }

  void listenToEventChannel() {
    const eventChannel = EventChannel('always_listener_event');
    eventChannel.receiveBroadcastStream().listen((dynamic data) {
      // Handle the received data
      processData(data);
    }, onError: (dynamic error) {
      // Handle errors
      print('Error on event channel: $error');
    });
  }

  processData(dynamic data) async {
    try {
      if (data is String) {
        print('Received data from native: $data');
      } else {
        print('Received data from native: ${data.runtimeType}');
        print(data.runtimeType == Uint8List);
        try {
          print(data.buffer);
        } catch (e) {
          print(e);
        }
        print('yes');
        ByteBuffer buffer = data.buffer;
        setState(() {
          recordings.add(buffer.asUint8List());
        });
      }
    } catch (e) {
      print(e);
    }
  }

  List<Uint8List> recordings = [];
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          leadingWidth: 0,
          title: const Row(
            children: [
              CircleAvatar(
                backgroundColor: Color(0xff70AFEC),
                child: Icon(
                  Icons.person,
                  color: Colors.white,
                ),
              ),
              SizedBox(
                width: 14,
              ),
              Text('Eren'),
            ],
          )),
      body: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            const SizedBox(
              height: 16,
            ),
            const Text(
              'Always Listening',
              style: TextStyle(fontSize: 24),
            ),
            const SizedBox(
              height: 16,
            ),
            const Text(
                'This app is always listening to you. Every 10 seconds, we send that audio to our STT API.\nThe last 3 transcripts will be hown on the screen. Additionally, we show a timer that indicates since when the app is listening'),
            const SizedBox(
              height: 53,
            ),
            const Text('API call counter'),
            Text(recordings.length.toString()),
            TextButton(
                onPressed: () async {
                  listenToEventChannel();
                  await Future.delayed(const Duration(seconds: 2));
                  callNativeCode();
                },
                child: const Text('trigger')),
            Expanded(
                child: ListView.separated(
                    shrinkWrap: true,
                    itemCount: recordings.length,
                    separatorBuilder: (context, index) {
                      return const SizedBox(
                        height: 5,
                      );
                    },
                    itemBuilder: (context, index) {
                      return ListTile(
                        onTap: () {
                          Navigator.of(context).push(MaterialPageRoute(
                              builder: (context) => AudioPlayerPage(
                                  audioData: recordings[index])));
                        },
                        tileColor: Colors.blue,
                        title: Text(index.toString()),
                      );
                    }))
          ])),
    );
  }
}
