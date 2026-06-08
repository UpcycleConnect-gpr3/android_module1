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

### Autres écrans à brancher (actuellement statiques)

| Écran                | Activité                | Données en dur à remplacer                            |
|----------------------|-------------------------|------------------------------------------------------|
| Abonnement en cours  | `ActivePlanActivity`    | `active_plan_name`, `active_plan_price` (strings)     |
| Historique           | `HistoryActivity`       | `history_date_1/2/3` (strings)                        |
| Notifications        | `NotificationsActivity` | état des 4 interrupteurs (aucun stockage)            |

> Idéalement, créer un modèle dédié par écran (ex. `Subscription`, `Invoice`,
> `NotificationSetting`) sur le modèle de `Parcel`, avec une méthode de données
> de test, puis remplacer par l'API.

---

## 2. Actions UI non câblées (à implémenter)

Ces éléments sont affichés mais **ne déclenchent encore aucune action** :

- **Login** (`MainActivity`) : le bouton « Se connecter » ne valide rien, il
  ouvre directement l'accueil. → Ajouter la vérification email/mot de passe
  (via API d'authentification).
- **Accueil** (`HomeActivity`) : la carte « Trouvez votre annonces »
  (`card_listings`) n'est branchée sur rien. (« Gérer mes abonnements » et
  « Recuperer mon colis » sont branchées.)
- **Abonnement en cours** : le bouton « Changer d'abonnements » (`change_plan`)
  n'a pas d'action.
- **Historique** : les liens « Télécharger » de chaque facture n'ont pas
  d'action (ni les cartes `card_invoice_1/2/3`).
- **Notifications** : l'état des interrupteurs n'est **pas persisté** et n'est
  relié à aucun backend.
- **Colis** : le QR est affiché mais non interactif (pas de scan ni d'action au
  clic).
- **Barre de navigation** : seul le bouton central « Accueil » (`nav_home`)
  agit (retour à l'accueil). Les icônes gauche (`nav_download`) et droite
  (`nav_upload`) n'ont pas d'action.

---

## 3. Navigation actuelle (rappel)

```
Login (MainActivity)
  └─> Accueil (HomeActivity)
        ├─ carte « Gérer mes abonnements » ─> Abonnements (SubscriptionsActivity)
        │      ├─ « Abonnements en cours »  ─> ActivePlanActivity
        │      ├─ « Historique »            ─> HistoryActivity
        │      └─ « Gerer mes notifications»─> NotificationsActivity
        ├─ carte « Trouvez votre annonces » ─> (non branchée)
        └─ carte « Recuperer mon colis »    ─> Mes colis (ParcelsActivity)
```

- Flèche retour (`back_button`) présente sur les sous-écrans des abonnements et
  sur l'écran colis → `finish()`.
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
