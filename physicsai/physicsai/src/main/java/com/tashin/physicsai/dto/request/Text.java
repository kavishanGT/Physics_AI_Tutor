package com.tashin.physicsai.dto.request;

import lombok.Data;

/**
 * Inbound webhook text payload from Meta's WhatsApp Cloud API.
 * Used when deserializing incoming messages received on the webhook.
 *
 * @see OutboundText for the outbound send-message counterpart
 */
@Data
public class Text {

    private String body;

}
