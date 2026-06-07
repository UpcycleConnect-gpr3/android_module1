# Charte graphique — android_module2

> Document de référence pour tout assistant IA (Claude, Mistral, etc.) et tout
> développeur qui travaille sur l'interface de ce projet Android (layouts XML,
> Material 3). **À lire avant de créer ou modifier un écran.**

---

## 1. Principes

- **Fond clair** : les écrans sont sur fond **blanc** (`@color/white`).
- **Cohérence** : on réutilise toujours les couleurs, espacements et styles
  définis ici. On n'introduit pas de valeur « en dur » au hasard.
- **Sobriété** : pas d'ombre (`elevation = 0dp`), bordures fines, coins **carrés**.
- **Accessibilité** : zones tactiles ≥ 48dp, contrastes lisibles, chaque icône a
  une `contentDescription`.

---

## 2. Couleurs (`res/values/colors.xml`)

| Rôle       | Nom ressource   | Hex       | Usage                                              |
|------------|-----------------|-----------|----------------------------------------------------|
| Primary    | `@color/primary`   | `#00433D` | Texte principal, titres, bordures, icônes, traits  |
| Secondary  | `@color/secondary` | `#D8FF4F` | Accent / mise en avant (ex. cercle de l'accueil)   |
| Tertiary   | `@color/tertiary`  | `#F5F5F5` | Fonds secondaires, séparateurs doux, surfaces      |
| Blanc      | `@color/white`     | `#FFFFFF` | Fond des écrans et des cartes                       |
| Noir       | `@color/black`     | `#000000` | À éviter pour le texte ; préférer `primary`         |

**Règles :**
- Le texte se met en **`primary`**, jamais en noir pur.
- **`secondary`** sert uniquement d'accent ponctuel (à ne pas utiliser comme
  couleur de texte sur fond blanc : contraste insuffisant).
- Toujours référencer une ressource (`@color/...`), jamais un hex en dur dans un
  layout.

---

## 3. Espacements (padding / marges)

Échelle utilisée (multiples de 4) :

| Valeur | Usage                                                            |
|--------|------------------------------------------------------------------|
| 4dp    | micro-espacement                                                 |
| 8dp    | petit espacement                                                 |
| 10dp   | padding vertical de la barre de navigation                       |
| 12dp   | padding interne des icônes (zone tactile)                        |
| 14dp   | espace entre le titre et l'action dans une carte                 |
| 16dp   | **espace entre deux cartes** ; marge haute sous l'en-tête        |
| 20dp   | **marge latérale standard de l'écran** (gauche/droite) ; en-tête |
| 22dp   | padding vertical interne d'une carte                             |

**Règles :**
- Marge horizontale d'un écran : **20dp** à gauche et à droite.
- Entre deux blocs/cartes : **16dp**.
- Le contenu défilant (`ScrollView`) ajoute un `paddingBottom` de **20dp**.

---

## 4. Typographie

| Élément            | Taille | Style | Couleur          |
|--------------------|--------|-------|------------------|
| Titre d'écran      | 22sp   | bold  | `@color/primary` |
| Titre de carte     | 16sp   | bold  | `@color/primary` |
| Lien d'action      | 16sp   | bold  | `@color/primary` |

- Tailles en **sp** (jamais en dp pour du texte).
- Les textes de carte sont **centrés** (`android:gravity="center"`).

---

## 5. Cartes (MaterialCardView)

Style de référence d'une carte :

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    app:cardBackgroundColor="@color/white"
    app:cardCornerRadius="0dp"      <!-- coins CARRÉS -->
    app:cardElevation="0dp"         <!-- pas d'ombre -->
    app:strokeColor="@color/primary"
    app:strokeWidth="1dp">
    ...contenu : paddingVertical=22dp, textes centrés...
</com.google.android.material.card.MaterialCardView>
```

**Règles :**
- Coins **carrés** : `cardCornerRadius="0dp"`.
- Pas d'ombre : `cardElevation="0dp"`.
- Bordure : `strokeColor="@color/primary"`, `strokeWidth="1dp"`.
- Contenu : padding vertical **22dp**, titre puis action centrés, **14dp** entre
  les deux.

---

## 6. Icônes

- Format : **VectorDrawable** (`res/drawable/ic_*.xml`), jamais de PNG.
- Teinte via `android:tint="@color/primary"`.
- Taille visuelle 24dp dans une zone tactile d'au moins 48dp (padding 12dp).
- Style : contour fin (famille type « Phosphor »), cohérent entre toutes les icônes.
- Accent ponctuel : un fond circulaire `secondary` + contour `primary`
  (cf. `bg_home_circle.xml`) pour l'élément actif/central.

---

## 7. Textes → toujours en ressources `@string`

**Aucun texte affiché ne doit être écrit en dur dans un layout.**
Tout passe par `res/values/strings.xml` pour permettre la traduction.

- Convention de nommage : `ecran_element[_variante]`
  (ex. `home_greeting`, `home_listings_title`, `home_listings_action`,
  `nav_home`).
- Pour ajouter une langue : créer `res/values-<code>/strings.xml`
  (ex. `values-en`, `values-es`) avec **les mêmes clés** traduites.
- Les `contentDescription` des icônes sont aussi des `@string`.

---

## 8. Checklist avant de livrer un écran

- [ ] Fond blanc (`@color/white`).
- [ ] Aucune couleur en hex en dur → uniquement `@color/...`.
- [ ] Aucun texte en dur → uniquement `@string/...`.
- [ ] Marges latérales de 20dp, espacement de 16dp entre blocs.
- [ ] Cartes : coins carrés, sans ombre, bordure 1dp `primary`.
- [ ] Texte en `primary`, jamais en noir pur.
- [ ] Icônes en VectorDrawable, teintées `primary`, zone tactile ≥ 48dp.
- [ ] Chaque icône a une `contentDescription`.
- [ ] Le projet compile (`./gradlew :app:assembleDebug`).
