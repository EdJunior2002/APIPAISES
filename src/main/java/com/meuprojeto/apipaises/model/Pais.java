package com.meuprojeto.apipaises.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @JsonProperty("name")
    private Nome nome;

    @Column(name = "capital")
    private String capital;

    @Column(name = "regiao")
    @JsonProperty("region")
    private String regiao;

    @Column(name = "populacao")
    @JsonProperty("population")
    private Long populacao;

    public static class Nome {
        private String common;

        public String getCommon() {
            return common;
        }

        public void setCommon(String common) {
            this.common = common;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Nome getNome() {
        return nome;
    }

    public void setNome(Nome nome) {
        this.nome = nome;
    }

    public List<String> getCapital() {
        if (capital != null && !capital.isEmpty()) {
            return List.of(capital.split(","));
        }
        return List.of();
    }

    public void setCapital(List<String> capitalList) {
        if (capitalList != null && !capitalList.isEmpty()) {
            this.capital = String.join(",", capitalList);
        } else {
            this.capital = null;
        }
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public Long getPopulacao() {
        return populacao;
    }

    public void setPopulacao(Long populacao) {
        this.populacao = populacao;
    }
}