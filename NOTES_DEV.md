# Notes de développement — android_module1

> Points importants à reprendre plus tard : branchements API, données de test,
> actions UI non câblées, dépendances. **À lire avant de continuer le projet.**

---

## 1. Branchement des API (données réelles)

Les écrans affichent pour l'instant des **données de test codées en dur**. Le
modèle de données est déjà en place : il suffira de remplacer la source de
données par la réponse réseau, **sans toucher au rendu**.

### Colis (`ParcelsActivity`)

- Données de test : `Parcel.sampleData()` dans
  `data/Parcel.kt`.
- **Pour brancher l'API** : remplacer l'appel `Parcel.sampleData()` dans
  `ParcelsActivity.renderParcels(...)` par la réponse réseau (liste de
  `Parcel`). Le rendu de la carte et la **génération du QR code** (à partir de
  `parcel.pickupCode`) ne changent pas.
- Modèle `Parcel` : `id`, `title`, `location`, `pickupCode`.
- Cas liste vide déjà géré (`parcels_empty`).

> ⚠️ L'appel réseau devra se faire **hors du thread principal** (coroutine /
> `lifecycleScope`) puis mettre à jour l'UI. Prévoir Retrofit + coroutines
> (cf. `android_module2` qui a déjà cette config dans son `build.gradle.kts`).

### Annonces (`ListingsActivity` + `ListingDetailActivity`)

- Données de test : `Listing.sampleData()` dans `data/Listing.kt`.
- **Pour brancher l'API** :
  - liste : remplacer l'appel `Listing.sampleData()` dans
    `ListingsActivity.onCreate(...)` par la réponse réseau (liste de
    `Listing`), sans toucher au rendu (`renderListings`) ;
  - détail : remplacer `Listing.findById(...)` dans
    `ListingDetailActivity.onCreate(...)` par un appel API (l'id de l'annonce
    est passé via l'extra `EXTRA_LISTING_ID`).
- Modèle `Listing` : `id`, `title`, `description`, `price`, `condition`,
  `location`, `seller`, `publishedDate`.
- Cas liste vide déjà géré (`listings_empty`).
- **Photo de l'annonce** : l'écran de détail affiche pour l'instant une image
  générique (`img_listing_placeholder`). Quand l'API fournira une URL d'image,
  ajouter un champ `imageUrl` au modèle et une lib de chargement (Coil ou
  Glide) pour remplir `detail_image`.

### Autres écrans à brancher (actuellement statiques)

| Écran                | Activité                | Données en dur à remplacer                            |
|----------------------|-------------------------|------------------------------------------------------|
| Abonnement en cours  | `ActivePlanActivity`    | `active_plan_name`, `active_plan_price` (strings)     |
| Historique           | `HistoryActivity`       | `history_date_1/2/3` (strings)                        |
| Notifications        | `NotificationsActivity` | état des 4 interrupteurs (aucun stockage)            |
| Paramètres           | `SettingsActivity`      | `settings_sample_username`, `settings_sample_email` (strings) |

> Idéalement, créer un modèle dédié par écran (ex. `Subscription`, `Invoice`,
> `NotificationSetting`) sur le modèle de `Parcel`, avec une méthode de données
> de test, puis remplacer par l'API.

---

## 2. Actions UI non câblées (à implémenter)

Ces éléments sont affichés mais **ne déclenchent encore aucune action** :

- **Login** (`MainActivity`) : le bouton « Se connecter » ne valide rien, il
  ouvre directement l'accueil. → Ajouter la vérification email/mot de passe
  (via API d'authentification).
- **Annonces** : le bouton « Contacter le vendeur » du détail affiche seulement
  un toast (`listing_contact_soon`) ; la mise en relation (messagerie/API)
  reste à brancher.
- **Abonnement en cours** : le bouton « Changer d'abonnements » (`change_plan`)
  n'a pas d'action.
- **Historique** : les liens « Télécharger » de chaque facture n'ont pas
  d'action (ni les cartes `card_invoice_1/2/3`).
- **Notifications** : l'état des interrupteurs n'est **pas persisté** et n'est
  relié à aucun backend.
- **Colis** : le QR est affiché mais non interactif (pas de scan ni d'action au
  clic).
- **Paramètres** : « Enregistrer » affiche seulement un toast (`settings_saved`),
  rien n'est persisté ni envoyé à une API. « Se déconnecter » revient au login
  sans invalider de session (pas encore de session réelle).
- **Barre de navigation** : le bouton central « Accueil » (`nav_home`) ramène à
  l'accueil et l'engrenage gauche (`nav_settings`) ouvre les paramètres. Seule
  l'icône droite (`nav_upload`) n'a pas d'action.

---

## 3. Navigation actuelle (rappel)

```
Login (MainActivity)
  └─> Accueil (HomeActivity)
        ├─ carte « Gérer mes abonnements » ─> Abonnements (SubscriptionsActivity)
        │      ├─ « Abonnements en cours »  ─> ActivePlanActivity
        │      ├─ « Historique »            ─> HistoryActivity
        │      └─ « Gerer mes notifications»─> NotificationsActivity
        ├─ carte « Trouvez votre annonces » ─> Les annonces (ListingsActivity)
        │      └─ clic sur une annonce ─> Détail (ListingDetailActivity)
        ├─ carte « Recuperer mon colis »    ─> Mes colis (ParcelsActivity)
        └─ engrenage de la nav bar          ─> Paramètres (SettingsActivity)
              └─ « Se déconnecter » ─> retour au Login (pile vidée)
```

- L'engrenage (`nav_settings`) est présent sur **tous** les écrans à nav bar et
  ouvre les paramètres.

- Flèche retour (`back_button`) présente sur les sous-écrans des abonnements,
  sur l'écran annonces et sur l'écran colis → `finish()`.
- L'accueil n'a **pas** de flèche retour (c'est le hub principal).

---

## 4. Dépendances

- **ZXing** (`com.google.zxing:core:3.5.3`) ajoutée pour la génération de QR
  codes (déclarée dans `gradle/libs.versions.toml` et `app/build.gradle.kts`).
  → **Un sync Gradle est nécessaire** dans Android Studio après récupération.

---

## 5. Divers

- Le projet n'a pas pu être compilé en ligne de commande (aucun JDK hors
  Android Studio sur la machine de dev actuelle). **Vérifier le build dans
  Android Studio** (`./gradlew :app:assembleDebug`).
- Respecter la **charte graphique** (`CHARTE_GRAPHIQUE.md`) : fond blanc,
  couleur `@color/primary`, coins carrés, pas d'ombre, textes en `@string`.
- L'écran 3 des notifications utilise de **vrais interrupteurs Material** à la
  place de l'image stock du maquettage. Couleur activée = `@color/secondary`
  (le vert citron du cercle « accueil » de la nav bar), via
  `res/color/switch_track_tint.xml` et `switch_thumb_tint.xml`.
