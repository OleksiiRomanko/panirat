import misc.Encryptor

val message="secret message"
val key="Key"
val encrypted=Encryptor.encrypt(key,message)
val decrypted=Encryptor.decrypt(key,encrypted)
val decryptedbad=Encryptor.decrypt(key+"23",encrypted)