package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "items")
@EqualsAndHashCode(exclude = {"name", "description", "available", "owner"})
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne
    @ToString.Exclude
    private User owner;

    @ManyToOne
    @ToString.Exclude
    private ItemRequest request;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Booking> bookings;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Comment> comments;

    public User getOwner() {
        return owner.toBuilder().build();
    }
}
