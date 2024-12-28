package ru.eugene.planningpoker.api.DTO;

import java.time.LocalDateTime;


public class PokerUserDTO {
    private Long id;
    private String userId;
    private String name;
    private String selectedNumber;
    private boolean leader;
    private LocalDateTime joinedAt;
    private String mood;

    public PokerUserDTO(final Long id, final String userId, final String name, final String selectedNumber, final boolean leader, final LocalDateTime joinedAt, final String mood) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.selectedNumber = selectedNumber;
        this.leader = leader;
        this.joinedAt = joinedAt;
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(final String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(final boolean leader) {
        this.leader = leader;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(final LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(final String mood) {
        this.mood = mood;
    }
}
