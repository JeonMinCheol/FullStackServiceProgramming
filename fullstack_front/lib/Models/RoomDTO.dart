class RoomDTO {
  final int id;
  final int userId1;
  final int userId2;
  String? lastComment;
  final String nickName;
  final String path;


  RoomDTO({
    required this.id,
    required this.nickName,
    required this.userId1,
    required this.userId2,
    required this.path,
    required this.lastComment,
  });

  factory RoomDTO.fromJson(Map<String, dynamic> json) {
    return RoomDTO(
      id: json['id'],
      nickName: json['nickName'],
      userId1: json['user1'],
      userId2: json['user2'],
      path: json['path'],
      lastComment: json['lastComment'],
    );
  }
}