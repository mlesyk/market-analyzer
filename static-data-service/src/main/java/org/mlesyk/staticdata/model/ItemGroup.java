package org.mlesyk.staticdata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "groups")
public class ItemGroup {
    @Id
    private Integer id;
    private Integer parentGroupId;
    private Boolean hasItems;
    private String name;

    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.ALL
    )
    private List<Item> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemGroup that = (ItemGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemGroup{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", parentGroupId=" + parentGroupId +
                ", hasItems=" + hasItems +
                ", items=" + items +
                '}';
    }
}