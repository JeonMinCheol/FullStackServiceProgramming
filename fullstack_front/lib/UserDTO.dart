class UserDTO {
  final int id;
  final String nickName;
  final String email;
  final int targetId;
  final String path;

  UserDTO({
    required this.id,
    required this.nickName,
    required this.email,
    required this.targetId,
    required this.path,
  });

  factory UserDTO.fromJson(Map<String, dynamic> json) {
    return UserDTO(
      id: json['id'],
      nickName: json['nickName'],
      email: json['email'],
      targetId: json['targetId'],
      path: json['path'],
    );
  }
}