import 'dart:convert';

import 'package:http/http.dart' as http;

class AudioSttService {
  Future<Map<String, dynamic>> sendAudioFile(String filePath) async {
    // Create a multipart request
    var request = http.MultipartRequest('POST',
        Uri.parse('https://35.207.149.36:443/stt_flutter_tech_assignment'));

    // Add headers
    request.headers.addAll({
      'Authorization': 'Bearer KsJ5Ag3',
    });

    // Add the file
    var file = await http.MultipartFile.fromPath('file', filePath);
    request.files.add(file);

    try {
      // Send the request
      var response = await request.send();

      if (response.statusCode == 200) {
        print('File uploaded successfully');
      } else {
        print('Failed to upload file');
      }

      final body = await response.stream.bytesToString();

      // Decode the JSON string
      final decodedBody = json.decode(body);

      return decodedBody;
    } catch (e) {
      print(e);
      throw (Exception());
    }
  }
}
