package com.example.utspam

data class User(
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var nik: String = "",
    var githubUsername: String = ""
) {
    constructor() : this("", "", "", "", "")
}
