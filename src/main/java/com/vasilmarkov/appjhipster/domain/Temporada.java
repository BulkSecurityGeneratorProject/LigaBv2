package com.vasilmarkov.appjhipster.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vasilmarkov.appjhipster.domain.util.CustomLocalDateSerializer;
import com.vasilmarkov.appjhipster.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A Temporada.
 */
@Entity
@Table(name = "TEMPORADA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Temporada implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "fecha_temporada", nullable = false)
    private LocalDate fecha_temporada;

    @ManyToOne
    private Liga liga;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "TEMPORADA_EQUIPOSACTUALES",
               joinColumns = @JoinColumn(name="temporadas_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="equiposactualess_id", referencedColumnName="ID"))
    private Set<Team> equiposActualess = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha_temporada() {
        return fecha_temporada;
    }

    public void setFecha_temporada(LocalDate fecha_temporada) {
        this.fecha_temporada = fecha_temporada;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Set<Team> getEquiposActualess() {
        return equiposActualess;
    }

    public void setEquiposActualess(Set<Team> teams) {
        this.equiposActualess = teams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Temporada temporada = (Temporada) o;

        if ( ! Objects.equals(id, temporada.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Temporada{" +
                "id=" + id +
                ", fecha_temporada='" + fecha_temporada + "'" +
                '}';
    }
}
