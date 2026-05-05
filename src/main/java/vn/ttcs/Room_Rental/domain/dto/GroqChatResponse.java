package vn.ttcs.Room_Rental.domain.dto;

public class GroqChatResponse {
    private String reply;

    public GroqChatResponse() {
    }

    public GroqChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}