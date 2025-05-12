package language;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Locale;

@ManagedBean
@SessionScoped
public class LanguageBean implements Serializable {

    private String language = "fr"; // Langue par défaut

    public Locale getLocale() {
        return Locale.forLanguageTag(language);
    }

    public String getLanguage() {
    	set();
        return language;
    }
    public void set() {
        // Met à jour la langue dans le contexte de JSF
        FacesContext.getCurrentInstance().getViewRoot().setLocale(Locale.forLanguageTag(language));
    }
    public void setLanguage(String language) {
        this.language = language;
        set();

        // Met à jour la langue dans le contexte de JSF
        FacesContext.getCurrentInstance().getViewRoot().setLocale(Locale.forLanguageTag(language));
    }
}
