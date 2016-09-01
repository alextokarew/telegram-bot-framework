package com.github.alextokarew.telegram.bots.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.github.alextokarew.telegram.bots.messages.Protocol.Responses.{OkWrapper, User}
import spray.json.DefaultJsonProtocol

/**
  * Created by alextokarew on 30.08.16.
  */
trait Protocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat4(User)
  implicit val okWrapperFormat = jsonFormat2(OkWrapper)

}

object Protocol {
  object Responses {

    case class OkWrapper(ok: Boolean, result: User)

    /**
      * This object represents a chat.
      * @param id Unique identifier for this chat. This number may be greater than 32 bits and some programming
      *           languages may have difficulty/silent defects in interpreting it. But it smaller than 52 bits,
      *           so a signed 64 bit integer or double-precision float type are safe for storing this identifier.
      * @param `type` Type of chat, can be either “private”, “group”, “supergroup” or “channel”
      * @param title Title, for channels and group chats
      * @param username Username, for private chats, supergroups and channels if available
      * @param first_name First name of the other party in a private chat
      * @param last_name Last name of the other party in a private chat
      */
    case class Chat(
      id: Long,
      `type`: String,
      title: Option[String],
      username: Option[String],
      first_name: Option[String],
      last_name: Option[String]
    )

    /**
      * This object represents a message.
      * @param message_id Unique message identifier
      * @param from Sender, can be empty for messages sent to channels
      * @param date Date the message was sent in Unix time
      * @param chat Conversation the message belongs to
      * @param forward_from For forwarded messages, sender of the original message
      * @param forward_from_chat For messages forwarded from a channel, information about the original channel
      * @param forward_date For forwarded messages, date the original message was sent in Unix time
      * @param reply_to_message For replies, the original message. Note that the Message object in this field will not
      *                         contain further reply_to_message fields even if it itself is a reply.
      * @param edit_date Date the message was last edited in Unix time
      * @param text For text messages, the actual UTF-8 text of the message, 0-4096 characters.
      * @param entities For text messages, special entities like usernames, URLs, bot commands, etc. that appear
      *                 in the text
      * @param audio Message is an audio file, information about the file
      * @param document Message is a general file, information about the file
      * @param photo Message is a photo, available sizes of the photo
      * @param sticker Message is a sticker, information about the sticker
      * @param video Message is a video, information about the video
      * @param voice Message is a voice message, information about the file
      * @param caption Caption for the document, photo or video, 0-200 characters
      * @param contact Message is a shared contact, information about the contact
      * @param location Message is a shared location, information about the location
      * @param venue Message is a venue, information about the venue
      * @param new_chat_member A new member was added to the group, information about them (this member may be
      *                        the bot itself)
      * @param left_chat_member A member was removed from the group, information about them (this member may be the
      *                         bot itself)
      * @param new_chat_title A chat title was changed to this value
      * @param new_chat_photo A chat photo was change to this value
      * @param delete_chat_photo Service message: the chat photo was deleted
      * @param group_chat_created Service message: the group has been created
      * @param supergroup_chat_created Service message: the supergroup has been created. This field can‘t be received in
      *                                a message coming through updates, because bot can’t be a member of a supergroup
      *                                when it is created. It can only be found in reply_to_message if someone replies
      *                                to a very first message in a directly created supergroup.
      * @param channel_chat_created Service message: the channel has been created. This field can‘t be received in
      *                             a message coming through updates, because bot can’t be a member of a channel when
      *                             it is created. It can only be found in reply_to_message if someone replies to
      *                             a very first message in a channel.
      * @param migrate_to_chat_id The group has been migrated to a supergroup with the specified identifier. This number
      *                           may be greater than 32 bits and some programming languages may have difficulty/silent
      *                           defects in interpreting it. But it smaller than 52 bits, so a signed 64 bit integer or
      *                           double-precision float type are safe for storing this identifier.
      * @param migrate_from_chat_id The supergroup has been migrated from a group with the specified identifier.
      *                             This number may be greater than 32 bits and some programming languages may
      *                             have difficulty/silent defects in interpreting it. But it smaller than 52 bits, so
      *                             a signed 64 bit integer or double-precision float type are safe for storing
      *                             this identifier.
      * @param pinned_message Specified message was pinned. Note that the Message object in this field will not contain
      *                       further reply_to_message fields even if it is itself a reply.
      */
    case class Message(
      message_id: Long,
      from: Option[User],
      date: Long,
      chat: Chat,
      forward_from: Option[User],
      forward_from_chat: Option[Chat],
      forward_date: Option[Long],
      reply_to_message: Option[Message],
      edit_date: Option[Long],
      text: Option[String],
      entities: Option[Seq[MessageEntity]],
      audio: Option[Audio],
      document: Option[Document],
      photo: Option[Seq[PhotoSize]],
      sticker: Option[Sticker],
      video: Option[Video],
      voice: Option[Voice],
      caption: Option[String],
      contact: Option[Contact],
      location: Option[Location],
      venue: Option[Venue],
      new_chat_member: Option[User],
      left_chat_member: Option[User],
      new_chat_title: Option[String],
      new_chat_photo: Option[Seq[PhotoSize]],
      delete_chat_photo: Option[Boolean],
      group_chat_created: Option[Boolean],
      supergroup_chat_created: Option[Boolean],
      channel_chat_created: Option[Boolean],
      migrate_to_chat_id: Option[Long],
      migrate_from_chat_id: Option[Long],
      pinned_message: Option[Message]
    )

    case class MessageEntity()

    /**
      * This object represents an incoming update.
      * Only one of the optional parameters can be present in any given update.
      * @param update_id The update‘s unique identifier. Update identifiers start from a certain positive number and
      *                  increase sequentially. This ID becomes especially handy if you’re using Webhooks, since it
      *                  allows you to ignore repeated updates or to restore the correct update sequence, should they
      *                  get out of order.
      * @param message New incoming message of any kind — text, photo, sticker, etc.
      * @param edited_message New version of a message that is known to the bot and was edited
      * @param inline_query New incoming inline query
      * @param chosen_inline_result The result of an inline query that was chosen by a user
      *                             and sent to their chat partner.
      * @param callback_query New incoming callback query
      */
    case class Update(
      update_id: Long,
      message: Option[Message],
      edited_message: Option[Message],
      inline_query: Option[InlineQuery],
      chosen_inline_result: Option[ChosenInlineResult],
      callback_query: Option[CallbackQuery]
    )

    case class User(
      id: Long,
      first_name: String,
      last_name: Option[String],
      username: Option[String]
    )
  }
}