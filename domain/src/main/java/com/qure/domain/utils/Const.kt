package com.qure.domain.utils


const val TAG = "AppTag"

// 파이어베이스 컬렉션 위치
const val BARCODE_COLLECTION_PATH = "barcode"
const val BARCODE_TIME_COLLECTION_PATH = "barcode_time"
const val CHAT_COLLECTION_PATH = "chat"
const val CHATROOMS_COLLECTION_PATH = "chatrooms"
const val COMMENTS_COLLECTION_PATH = "comments"
const val MEETING_COLLECTION_PATH = "meething"
const val POSTS_COLLECTION_PATH = "posts"
const val SETTING_COLLECTION_PATH = "setting"
const val USERS_COLLECTION_PATH = "users"
const val REPLY_COLLECTION_PATH = "reply"


// 파이어베이스 컬렉션 필드
// Barcode
const val BARCODE_FIELD = "barcode"

//BarcodeScan
const val MEETING_FIELD =  "meeting"

//BarcodeTime
const val BARCODE_TIME_FIELD =  "barcodetime"

//ChatMessage
const val USERNAME_FIELD = "usernm"
const val MESSAGETYPE_FIELD ="messagetype"
const val READ_USERS_FIELD = "readUsers"

//ChatRoom
const val ROOM_FIELD = "room"
const val PHOTO_FIELD = "photo"
const val LAST_MESSAGE_FIELD= "lastmsg"
const val LAST_DATE_FIELD = "lastDate"
const val USER_COUNT_FIELD = "userCount"
const val UNREAD_COUNT_FIELD = "unreadCount"
const val USERS_FIELD = "users"

//Comments
const val COMMENTS_UID_FIELD = "comments_uid"
const val COMMENTS_USER_NAME_FIELD = "comments_usernm"
const val COMMENTS_USER_IMAGE_FIELD = "comments_userimage"
const val COMMENTS_CONTENT_FIELD = "comments_content"
const val COMMENTS_TIMESTAMP_FIELD = "comments_timestamp"
const val COMMENTS_REPLY_TIMESTAMP_FIELD = "comments_replyTimeStamp"
const val COMMENTS_LIKE_COUNT_FIELD = "comments_likeCount"
const val COMMENTS_POST_KEY_FIELD = "comments_postkey"
const val COMMENTS_COMMENT_KEY_FIELD = "comments_commentskey"
const val COMMENTS_DEPTH_FIELD = "comments_depth"
const val COMMENTS_REPLY_KEY_FIELD = "comments_replyKey"

//Post
const val WRITER_FIELD = "writer"
const val CATEGORY_FIELD = "category"
const val CONTENT_FIELD = "content"

const val KEY_FIELD = "key"
const val LIKE_COUNT_FIELD = "likecount"
const val COMMENTS_COUNT_FIELD = "commentsCount"
const val POST_IMAGES_FIELD = "postImages"

//PostImage
const val POST_IMAGE_FIELD = "postImage"

//Setting
const val VIBRATION_FIELD = "vibration"
const val SOUND_FIELD = "sound"
const val NOTIFICATION_TIME_FIELD = "notification_time"

//UploadImage
const val IMAGE_PATH_FIELD = "imagePath"

//User
const val USER_ID_FIELD = "userid"
const val USER_NAME_FIELD = "usernm"
const val TOKEN_FIELD = "token"
const val USER_PHOTO_FIELD = "userphoto"
const val USER_MESSAGE_FIELD = "usermsg"

//Common Field
const val UID_FIELD = "uid"
const val POST_KEY_FIELD = "postkey"
const val MESSAGE_FIELD = "message"
const val TITLE_FIELD = "title"
const val ROOM_ID_FIELD ="roomId"
const val TIMESTAMP_FIELD = "timestamp"
const val USER_IMAGE_FIELD = "userimage"

// Collection Group
const val IMAGES_COLLECTION_GROUP = "images"

// Upload Field
const val POST_IMAGE_PATH = "post_image/"
const val PROFILE_IMAGE_PATH = "profile_image/"