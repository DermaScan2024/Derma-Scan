package com.example.dermascan.Connection



import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

import android.graphics.Bitmap
import kotlinx.datetime.Clock

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


val supabase = createSupabaseClient(
    supabaseUrl = "https://gxbtcmgmkpvldeespbet.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd4YnRjbWdta3B2bGRlZXNwYmV0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjI3OTQxMTQsImV4cCI6MjAzODM3MDExNH0.RtGAUTvB6eOpag68P_cUP4epSH8f_5xHOd-cBQymU-M"
) {

    install(Postgrest)
    install(Storage)
    //install other modules
}


@Serializable
data class InfoUser(
    val id: Int,
    val name: String,
    val email: String,
    val image: String?,
)

@Serializable
data class NewInfoUser(
    val name: String,
    val email: String,
    val password: String,
)


@Serializable
data class PhotosInfo(
    val date: String,
    val percentage: Float,
    val id_user: Int,
)

public  class ConnectBD (){
    private var connectBD : ConnectBD? = null;

    private class ConnectBD constructor(){
    }

    companion object {

        private fun EncryptPassword(password: String): String = runBlocking {
            val hashPassWordBytes = MessageDigest.getInstance("MD5")
            val hashbytes = hashPassWordBytes.digest(password.toByteArray(StandardCharsets.UTF_8))
            val passwordCrypto = StringBuilder()
            for (b in hashbytes) {
                passwordCrypto.append(String.format("%02x", b))
            }
            passwordCrypto.toString()
        }

        public fun GetUser(name: String, password: String): String = runBlocking {
            withContext(Dispatchers.IO) {
                try {
                    val user = supabase.from("User")
                        .select(columns = Columns.list("id,name,email,image")) {
                            filter {
                                and {
                                    or {
                                        eq(column = "email", value = name)
                                        eq(column = "name", value = name)
                                    }
                                    eq(column = "password", EncryptPassword(password))
                                }
                            }
                        }.decodeSingle<InfoUser>()
                    "${user.name};${user.email};${user.id};${user.image};${password}"
                } catch (e: Exception) {
                    "UserNot"
                }


            }
        }

        public fun InsertUser(email: String, name: String, password: String): Boolean =
            runBlocking {

                withContext(Dispatchers.IO) {
                    try {
                        val user =
                            supabase.from("User").select(columns = Columns.list("id,name,email")) {
                                filter {
                                    and {
                                        eq(column = "email", value = email)
                                        eq(column = "name", value = name)
                                        eq(column = "password", value = EncryptPassword(password))
                                    }
                                }
                            }.decodeSingle<InfoUser>()
                        false
                    } catch (e: Exception) {
                        try {
                            val newUser = NewInfoUser(
                                name = name,
                                email = email,
                                password = EncryptPassword(password)
                            )
                            supabase.from("User").insert(newUser)
                            true
                        } catch (err: Exception) {
                            false
                        }

                    }


                }
            }

        public fun UpdateUser(
            oldInformations: Array<String>,
            newInformations: Array<String>
        ): Boolean = runBlocking {
            try {
                supabase.from("User").update(
                    {
                        set("name", newInformations[0])
                        set("email", newInformations[1])
                        set("password", EncryptPassword(newInformations[2]))
                    }
                ) {
                    filter {
                        and {
                            eq("name", oldInformations[0])
                            eq("email", oldInformations[1])
                            eq("password", EncryptPassword(oldInformations[2]))
                        }
                    }
                }
                true
            } catch (e: Exception) {
                false
            }
        }

        public fun UpdatePassWord(newPassword : String, email: String): Boolean = runBlocking {
            try {
                supabase.from("User").update(
                    {
                        set("password", EncryptPassword(newPassword))
                    }
                ) {
                    filter {
                        and {
                            eq("email",email)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                false
            }
        }

        public fun InsertIconUser(name: String, email: String, bitmap: Bitmap, id: Int): Boolean =
            runBlocking {
                try {
                    supabase.from("User").update(
                        {
                            set("image", "${email}${id}.png")
                        }
                    ) {
                        filter {
                            and {
                                eq("email", email)
                                eq("name", name)
                            }
                        }
                    }


                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    val byteArray = outputStream.toByteArray()
                    val bucket = supabase.storage.from("bucketImages")
                    bucket.upload("${email}${id}.png", byteArray, upsert = true)
                    true
                } catch (e: Exception) {
                    false
                }

            }

        public fun GetIconUser(name: String, email: String): ByteArrayOutputStream = runBlocking {
            val user = supabase.from("User").select(columns = Columns.list("id,name,email,image")) {
                filter {
                    and {

                        eq(column = "email", value = email)
                        eq(column = "name", value = name)

                    }
                }
            }.decodeSingle<InfoUser>()
            if (user.image.equals("StandardIcon.png") or user.image.equals(null)) {
                ByteArrayOutputStream()
            } else {
                val bucket = supabase.storage.from("bucketImages")
                val bytes = bucket.downloadAuthenticated("${user.image}")
                val outputStream = ByteArrayOutputStream()
                outputStream.write(bytes)
                outputStream
            }

        }

        public fun InsertPhotoRecord(percentage: Float, idUser: Int): Boolean = runBlocking {
            withContext(Dispatchers.IO) {
                try {
                    val time = Clock.System.now().toString()
                    val date = time.split('T')[0]
                    val newPhoto =
                        PhotosInfo(id_user = idUser, percentage = percentage, date = date)

                    supabase.from("Photos").insert(newPhoto)
                    true
                } catch (e: Exception) {
                    false
                }

            }
        }

        public fun GetPhotoRecord(idUser: Int): List<PhotosInfo> = runBlocking {
            withContext(Dispatchers.IO) {
                try {

                    val photos = supabase.from("Photos")
                        .select(columns = Columns.list("id_photo,id_user,date,percentage")) {
                            filter {
                                and {
                                    eq(column = "id_user", idUser)
                                }
                            }
                        }.decodeList<PhotosInfo>()
                    photos
                } catch (e: Exception) {
                    val photos: List<PhotosInfo> = listOf()
                    photos
                }

            }
        }
    }
}


