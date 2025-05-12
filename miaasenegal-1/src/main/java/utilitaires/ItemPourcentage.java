package utilitaires;

import java.io.Serializable;

public class ItemPourcentage implements Serializable {
    private String libelle;
    private Double pourcentage;
    private Long count; // Nombre de personnes ayant répondu pour cet item

    public ItemPourcentage(String libelle, Double pourcentage, Long count) {
        this.libelle = libelle;
        this.pourcentage = pourcentage;
        this.count = count;
    }

    // Getters et setters
    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(Double pourcentage) {
        this.pourcentage = pourcentage;
    }
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
}
