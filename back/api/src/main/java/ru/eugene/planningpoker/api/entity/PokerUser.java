package ru.eugene.planningpoker.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "poker_user")
@AllArgsConstructor
@NoArgsConstructor
public class PokerUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;
    private String sessionId;
    private String selectedNumber;
    private boolean leader;
    private LocalDateTime joinedAt;
    private String mood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonBackReference
    private Room room;

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(final Room room) {
        this.room = room;
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
