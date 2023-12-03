class FriendDTO {
  final int id;
  final String nickName;
  final String email;
  final int targetId;
  final String path;

  FriendDTO({
    required this.id,
    required this.nickName,
    required this.email,
    required this.targetId,
    required this.path,
  });

  factory FriendDTO.fromJson(Map<String, dynamic> json) {
    return FriendDTO(
      id: json['id'],
      nickName: json['nickName'],
      email: json['email'],
      targetId: json['targetId'],
      path: json['path'],
    );
  }
}