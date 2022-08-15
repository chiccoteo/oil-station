package com.sigma.oilstation.entity;

import javax.persistence.Column;

import com.sigma.oilstation.entity.template.AbsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinancialCategory extends AbsEntity {
    @Column(nullable = false)
    private String name;
}
