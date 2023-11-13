class UserDTO {
  final int id;
  final String name;
  final String nickName;
  final String email;
  final String role;
  final String path;

  UserDTO({
    required this.id,
    required this.nickName,
    required this.name,
    required this.email,
    required this.role,
    required this.path,
  });

  factory UserDTO.fromJson(Map<String, dynamic> json) {
    return UserDTO(
      id: json['id'],
      name: json['name'],
      nickName: json['nickName'],
      email: json['email'],
      role: json['role'],
      path: json['path'],
    );
  }
}