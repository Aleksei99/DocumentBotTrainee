package com.smuraha.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "raw_data")
@TypeDef(name = "jsonb",typeClass = JsonBinaryType.class)
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Update event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawData rawData = (RawData) o;
        return Objects.equals(event, rawData.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event);
    }
}