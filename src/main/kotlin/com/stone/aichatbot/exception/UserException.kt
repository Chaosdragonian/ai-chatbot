package com.stone.aichatbot.exception


sealed class UserException(
    override val message: String,
) : RuntimeException(message)

class EmailAlreadyExistsException : UserException("이미 존재하는 이메일입니다.")
class EmailNotFoundException : UserException("존재하지 않는 이메일입니다.")
class PasswordMismatchException : UserException("비밀번호가 일치하지 않습니다.")
