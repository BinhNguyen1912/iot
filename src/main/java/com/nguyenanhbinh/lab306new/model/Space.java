package com.nguyenanhbinh.lab306new.model;

import jakarta.persistence.*;

@Entity
@Table(name = "spaces")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // My Home

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // ===== GETTERS / SETTERS =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
