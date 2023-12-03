class ChatDTO {
  final String? translate;
  final String? nickName;
  final String? image;
  final String? text;
  final DateTime? time;

  ChatDTO({
    required this.translate,
    required this.nickName,
    required this.image,
    required this.text,
    required this.time,
  });

  factory ChatDTO.fromJson(Map<String, dynamic> json) {
    String img = (json['image'] == null) ? "null" : json['image'];
    String text = (json['text'] == null) ? "null" : json['text'];
    String translate = (json['translate'] == null) ? "null" : json['translate'];
    return ChatDTO(
      translate: translate,
      nickName: json['nickName'],
      image: img,
      text: text,
      time : DateTime.parse(json["time"])
    );
  }
}