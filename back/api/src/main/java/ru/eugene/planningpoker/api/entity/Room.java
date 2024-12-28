package ru.eugene.planningpoker.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @Column(nullable = false)
    private boolean votingComplete = false;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PokerUser> users = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public boolean isVotingComplete() {
        return votingComplete;
    }

    public void setVotingComplete(final boolean votingComplete) {
        this.votingComplete = votingComplete;
    }

    public List<PokerUser> getUsers() {
        return users;
    }

    public void setUsers(final List<PokerUser> users) {
        this.users = users;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }
}
