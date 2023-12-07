import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/services.dart';

class AudioPlayerPage extends StatefulWidget {
  final Uint8List audioData;

  const AudioPlayerPage({super.key, required this.audioData});

  @override
  _AudioPlayerPageState createState() => _AudioPlayerPageState();
}

class _AudioPlayerPageState extends State<AudioPlayerPage> {
  late AudioPlayer _audioPlayer;
  bool _isPlaying = false;

  Uint8List? assetList;

  @override
  void initState() {
    super.initState();
    _audioPlayer = AudioPlayer();
    _initPlayer();
  }

  void _initPlayer() async {
    await _audioPlayer.setVolume(1.0);
    print(widget.audioData.length);
  }

  Future<void> _playAudio() async {
    await _audioPlayer.play(BytesSource(widget.audioData));
    setState(() {
      _isPlaying = true;
    });
  }

  Future<void> _pauseAudio() async {
    await _audioPlayer.pause();
    setState(() {
      _isPlaying = false;
    });
  }

  Future<void> get() async {
    const assetPath = 'assets/sample_mp3.mp3';
    final byteData = await rootBundle.load(assetPath);
    final bytes = byteData.buffer.asUint8List();
    setState(() {
      assetList = bytes;
    });
  }

  @override
  void dispose() {
    _audioPlayer.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Audio Player'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextButton(
                onPressed: () async {
                  print(widget.audioData.length);
                  await get();
                  print(assetList?.length);
                  _audioPlayer.play(BytesSource(widget.audioData));
                },
                child: const Text('set')),
            IconButton(
              icon: Icon(_isPlaying ? Icons.pause : Icons.play_arrow),
              onPressed: () {
                if (_isPlaying) {
                  _pauseAudio();
                } else {
                  _playAudio();
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
