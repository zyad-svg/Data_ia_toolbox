package com.toolbox.module4;

public final class Module4Knowledge {

    private Module4Knowledge() {}

    public static final String[][] KNOWLEDGE = {

        // ── Commandes ────────────────────────────────────────────────────────
        {"commande",     "Une commande passe par 4 étapes : 1) Confirmation — email immédiat après paiement. 2) Préparation — 24 h ouvrées pour emballer votre colis. 3) Expédition — email avec numéro de suivi. 4) Livraison — 3-5 jours standard, 24 h express. Numéro de commande = 8 chiffres dans l'email de confirmation."},
        {"statut",       "Les statuts possibles sont : 'En attente de paiement', 'En préparation', 'Expédié', 'En cours de livraison', 'Livré', 'Retour en cours'. Chaque changement déclenche un email automatique."},
        {"achat",        "Historique complet disponible dans votre espace client. Chaque ligne indique : date, référence, articles, montant TTC, statut et lien vers la facture PDF."},
        {"panier",       "Le panier est sauvegardé 7 jours. Prix verrouillé au moment de la mise au panier. Si un article est épuisé avant validation, il est retiré automatiquement avec un email d'alerte."},
        {"numéro",       "Votre numéro de commande (8 chiffres) figure dans l'email de confirmation. Il est indispensable pour tout contact SAV, suivi transporteur ou retour."},
        {"historique",   "L'historique de vos commandes est conservé 5 ans dans votre espace client. Vous pouvez filtrer par date, statut ou montant et télécharger chaque facture en PDF."},
        {"modifier commande", "Une commande peut être modifiée dans l'heure suivant la validation, avant sa mise en préparation. Passé ce délai, il faut annuler et repasser commande."},
        {"confirmation", "L'email de confirmation arrive en moins de 5 minutes. Si absent, vérifiez les spams. Le sujet est 'Votre commande #XXXXXXXX a bien été enregistrée'."},

        // ── Paiement ─────────────────────────────────────────────────────────
        {"paiement",     "Moyens acceptés : Visa, Mastercard, CB, PayPal, Apple Pay, Google Pay, virement SEPA et paiement en 3x sans frais (commandes ≥ 100 €). Tout est sécurisé SSL + 3D Secure."},
        {"carte",        "Les données de carte sont chiffrées AES-256 et jamais stockées. En cas de refus : vérifiez le plafond CB, activez l'achat en ligne auprès de votre banque, et assurez-vous que le 3D Secure est validé sur votre téléphone."},
        {"prix",         "Tous les prix sont affichés TTC (TVA 20 % incluse). Le prix affiché sur la fiche produit est garanti jusqu'à validation du paiement. Aucun frais caché."},
        {"tarif",        "Nos tarifs sont révisés chaque semaine. Le prix le plus bas des 30 derniers jours est affiché sous forme de 'Prix de référence' sur chaque fiche produit."},
        {"promotion",    "Les promotions actives apparaissent : 1) Bannière d'accueil. 2) Onglet 'Offres'. 3) Fiche produit (badge rouge '–X%'). Elles ne sont pas cumulables sauf mention contraire."},
        {"reduction",    "Le code promo se saisit étape 'Paiement', champ 'Code avantage'. Il est appliqué immédiatement. Un code = une utilisation par compte. Vérifiez la date d'expiration."},
        {"code promo",   "Format standard : 8 caractères alphanumériques majuscules (ex: SUMMER24). Conditions : un usage par compte, non cumulable, valable sur articles non soldés sauf mention contraire."},
        {"remise",       "Les remises fidélité s'accumulent : Bronze (5 % dès 200 € d'achats), Argent (10 % dès 500 €), Or (15 % dès 1000 €). Le statut est recalculé chaque 1er du mois."},
        {"paiement 3x",  "Le paiement en 3x sans frais est disponible dès 100 €. 1er prélèvement à la commande, 2e à J+30, 3e à J+60. Aucun intérêt ni frais de dossier."},
        {"refus paiement","Causes courantes de refus : plafond CB atteint, 3D Secure non validé, carte non activée pour l'e-commerce, fonds insuffisants. Contactez votre banque ou essayez un autre moyen de paiement."},
        {"virement",     "IBAN : FR76 1234 5678 9012 3456 7890 189 — BIC : BNPAFRPP. Indiquez votre numéro de commande en référence. Délai de crédit : 2-3 jours ouvrés. La commande est bloquée jusqu'à réception."},
        {"prelevement",  "Les prélèvements du paiement en 3x sont automatiques sur la carte utilisée. Dates : J, J+30, J+60. En cas d'échec, un email est envoyé avec un lien pour régulariser sous 48h."},

        // ── Livraison ────────────────────────────────────────────────────────
        {"livraison",    "Tarifs : Standard gratuit dès 50 €, sinon 4,99 €, délai 3-5 jours. Express 9,99 €, livraison J+1 si commande avant 14h. Point Relais 2,99 €, 2-4 jours. DOM-TOM 12,99 €, 7-12 jours."},
        {"delai",        "Délai total = traitement (24 h ouvrées) + transport. Standard : 3-5 jours. Express : 24 h. Point Relais : 2-4 jours. Dimanche et jours fériés non comptés."},
        {"expedition",   "Votre colis part de notre entrepôt (Île-de-France) sous 24 h ouvrées. Email d'expédition avec numéro de suivi envoyé dès la prise en charge par le transporteur."},
        {"colis",        "Si le colis est endommagé : 1) Refusez la livraison ou émettez des réserves écrites sur le bon du livreur. 2) Photographiez sous 24h. 3) Envoyez photos + numéro de commande à sav@toolbox.fr. Remplacement ou remboursement sous 48h."},
        {"suivi",        "Suivi en temps réel sur le site du transporteur avec le numéro fourni par email. Mise à jour toutes les 2-4h. En cas de statut bloqué plus de 48h sans mouvement, contactez le support."},
        {"transporteur", "Colissimo (standard), Chronopost (express), Mondial Relay (point relais), DHL (DOM-TOM et international). Le transporteur est choisi automatiquement selon votre adresse et mode de livraison."},
        {"adresse",      "Modification d'adresse possible avant expédition depuis 'Mon compte'. Après expédition : contactez le support dans l'heure — une demande de déviation peut être faite auprès du transporteur (non garantie)."},
        {"retard livraison","Délai dépassé ? Vérifiez d'abord le suivi transporteur (parfois en avance sur l'email). Si bloqué 48h sans mise à jour, contactez le support avec votre numéro de commande. Dédommagement possible si retard > 7 jours ouvrés."},
        {"express",      "Livraison Express : commande avant 14h → livraison le lendemain ouvré. Commande après 14h → surlendemain. Non disponible le week-end. Zone France métropolitaine uniquement."},
        {"point relais", "Plus de 10 000 points Mondial Relay en France. Délai 2-4 jours, 2,99 €. Le colis est conservé 14 jours. SMS de notification à l'arrivée. Carte nationale requise pour retrait."},
        {"international","Livraison disponible dans 30 pays. Délai : 5-10 jours UE, 10-15 jours hors UE. Frais de port variables selon destination. Douanes : frais à la charge du destinataire hors UE."},
        {"domicile",     "Livraison à domicile en boîte aux lettres si format compatible, sinon contre signature. Deuxième tentative J+1. Puis avis de passage + dépôt en bureau de poste ou point relais pendant 15 jours."},

        // ── Retours & remboursements ──────────────────────────────────────────
        {"retour",       "Délai légal de rétractation : 14 jours. Notre politique étendue : 30 jours. L'article doit être neuf, non utilisé, dans son emballage d'origine avec étiquettes. Alimentaire et produits personnalisés non retournables."},
        {"remboursement","Remboursement sous 5-7 jours ouvrés après réception du retour. Crédit sur le moyen de paiement initial. Suivi par email à chaque étape. Stripe/PayPal peut prendre 3-5 jours supplémentaires selon votre banque."},
        {"rembourser",   "Pour initier un retour : 1) Allez dans 'Mes commandes > Retourner un article'. 2) Cochez les articles et motif. 3) Imprimez l'étiquette prépayée. 4) Déposez en point relais sous 7 jours. 5) Suivi par email."},
        {"echange",      "Échange sous 30 jours pour même article en taille/couleur différente, ou autre article de valeur identique ou supérieure. L'échange pour article inférieur génère un avoir. Frais de retour offerts."},
        {"annuler",      "Annulation dans l'heure suivant la commande : bouton 'Annuler' disponible dans 'Mes commandes'. Après 1h et avant expédition : contactez le support (réponse < 1h en heures ouvrées). Après expédition : procédez à un retour à réception."},
        {"annulation",   "Commande annulée → remboursement sous 24h si paiement par carte (délai banque : 3-5 jours). PayPal : immédiat. Virement : 3-5 jours ouvrés. Un email de confirmation d'annulation vous est envoyé."},
        {"avoir",        "L'avoir est valable 1 an. Il s'applique automatiquement à la prochaine commande ou manuellement en sélectionnant 'Utiliser mon avoir' au paiement. Solde visible dans 'Mon compte > Mes avoirs'."},
        {"garantie",     "Garantie légale vices cachés : 2 ans. Garantie légale conformité : 2 ans. Garantie commerciale : selon fabricant (indiquée sur la fiche produit). Pour l'activer : conservez votre facture et contactez-nous."},
        {"defectueux",   "Produit défectueux : 1) Photographiez le défaut. 2) Envoyez à sav@toolbox.fr avec N° de commande + description. 3) Échange ou remboursement complet sous 48h. Frais de retour offerts. Pas besoin d'attendre 30 jours."},

        // ── Compte & sécurité ────────────────────────────────────────────────
        {"compte",       "Votre espace client regroupe : historique commandes, factures PDF, adresses sauvegardées, programmes fidélité, alertes stock, et gestion des données personnelles (RGPD)."},
        {"mot de passe", "Réinitialisation : cliquez 'Mot de passe oublié', entrez votre email → lien valable 30 minutes. Exigences : 8 caractères min, 1 majuscule, 1 chiffre, 1 symbole. Le lien expire après utilisation."},
        {"connexion",    "Impossible de vous connecter ? Causes fréquentes : mauvais email (essayez vos alias), Caps Lock activé, compte bloqué après 5 tentatives (déverrouillage par email). Utilisez 'Mot de passe oublié' en premier recours."},
        {"email",        "Changement d'email : 'Mon compte > Mes informations > Modifier email'. Un email de confirmation est envoyé à la NOUVELLE adresse. L'ancien email reste actif jusqu'à validation (sécurité)."},
        {"inscription",  "Inscription en 2 minutes : email + mot de passe + prénom. Email de validation à cliquer (valable 24h). Sans validation, le compte est en mode limité (consultation uniquement)."},
        {"supprimer compte","Suppression RGPD : 'Mon compte > Mes données > Supprimer mon compte'. Toutes vos données sont effacées sous 30 jours (sauf obligations légales : factures conservées 10 ans). Irréversible."},
        {"données personnelles","RGPD : vous pouvez exporter, modifier ou supprimer vos données depuis 'Mon compte > Mes données'. Export disponible en JSON ou CSV sous 24h. DPO joignable à dpo@toolbox.fr."},
        {"newsletter",   "Gestion des emails marketing : 'Mon compte > Préférences > Notifications'. Désabonnement aussi via le lien en bas de chaque email (effectif sous 48h)."},
        {"double authentification","2FA disponible via application (Google Authenticator, Authy) ou SMS. Activation dans 'Mon compte > Sécurité'. Fortement recommandé pour protéger vos paiements sauvegardés."},

        // ── Produits & stock ─────────────────────────────────────────────────
        {"produit",      "Chaque fiche produit indique : description complète, composition/matière, dimensions, poids, note client (/5 avec verbatim), disponibilité en temps réel, et délai de livraison estimé."},
        {"stock",        "Stock mis à jour toutes les 15 minutes. 'En stock' = expédition sous 24h. 'Stock limité' = moins de 5 unités. 'Rupture' = épuisé. 'Sur commande' = 7-21 jours de réapprovisionnement."},
        {"disponible",   "Alerte de disponibilité : sur la fiche produit > bouton 'M'avertir'. Email envoyé dès remise en stock. L'alerte reste active 90 jours puis se désactive automatiquement."},
        {"rupture",      "Délai de réapprovisionnement indiqué sur la fiche produit. Généralement 1-3 semaines pour les articles courants. Vous pouvez pré-commander certains articles épuisés avec livraison dès réception du stock."},
        {"taille",       "Guide des tailles sur chaque fiche produit (cm et équivalences internationales). En cas de doute : prenez la taille supérieure. Notre politique retour échange de taille est gratuite sous 30 jours."},
        {"avis",         "Les avis sont vérifiés (achat confirmé requis). Note moyenne calculée sur les 12 derniers mois. Vous pouvez laisser un avis 7 jours après livraison depuis 'Mes commandes > Laisser un avis'."},
        {"comparaison",  "Comparez jusqu'à 4 produits simultanément via le bouton 'Comparer' sur chaque fiche. Le tableau comparatif affiche toutes les caractéristiques techniques côte à côte."},
        {"marque",       "Nous proposons 200+ marques. Chaque marque a sa page dédiée avec l'ensemble de sa gamme, l'histoire de la marque et les certifications produits."},
        {"certifications","Certifications affichées sur les fiches produit : CE, NF, ISO, écolabels (AB, Ecocert, etc.). Documents de conformité téléchargeables sur demande à conformite@toolbox.fr."},
        {"photo produit","Les photos sont réalisées en studio sur fond blanc + photos lifestyle. Zoom x10 disponible. Vidéo 360° sur certains articles. Les couleurs peuvent légèrement varier selon l'écran."},

        // ── Facturation & fiscalité ──────────────────────────────────────────
        {"facture",      "Factures PDF disponibles dès expédition dans 'Mes commandes > Télécharger la facture'. Conformes aux normes comptables. Numérotation séquentielle. Archivage 5 ans dans votre espace client."},
        {"recu",         "Le reçu arrive par email dans les 5 minutes après le paiement. Il inclut : liste des articles, montants HT/TVA/TTC, mode de paiement, adresses de facturation et livraison."},
        {"tva",          "TVA 20 % sur la majorité des articles. Taux réduit 5,5 % sur certains produits alimentaires et livres. TVA 10 % sur la restauration. Détail sur chaque facture. Exportations hors UE exonérées."},
        {"facture professionnelle","Facture pro avec SIRET/TVA intracommunautaire : renseignez vos informations dans 'Mon compte > Mes informations > Compte professionnel'. Toutes vos futures factures incluront ces données."},
        {"note de frais","Les factures téléchargeables depuis votre espace client sont acceptées par tous les logiciels comptables. Format PDF/A archivable. Si votre service comptable exige un format spécifique, contactez-nous."},

        // ── Support & contact ────────────────────────────────────────────────
        {"contact",      "Support disponible sur 3 canaux : 1) Chat (ici) : 24h/24. 2) Email support@toolbox.fr : réponse sous 4h ouvrées. 3) Téléphone 01 23 45 67 89 : lun-ven 9h-18h, sam 10h-16h."},
        {"telephone",    "Numéro : 01 23 45 67 89 — Lun-Ven 9h-18h, Sam 10h-16h. Temps d'attente moyen : 3 min. Pour éviter l'attente : email ou chat disponibles 24h/24 avec même niveau de service."},
        {"horaires",     "Service client : Lun-Ven 9h-18h, Sam 10h-16h. Chat : 24h/24 7j/7. Email : 24h/24, réponse sous 4h ouvrées. Urgences (colis perdu, paiement frauduleux) : ligne dédiée 24h/24 au 01 23 45 67 00."},
        {"humain",       "Pour parler à un conseiller : option 1 du serveur vocal au 01 23 45 67 89 (lun-ven 9h-18h). Ou écrivez 'Conseiller humain' ici — un agent reprend ce chat dans les 5 minutes ouvrées."},
        {"reclamation",  "Réclamation formelle : formulaire dans 'Mon compte > Réclamations' ou email à reclamations@toolbox.fr. Accusé de réception automatique. Réponse avec N° de dossier sous 5 jours ouvrés. Médiateur : en dernier recours sur mediateur.fr."},
        {"rappel",       "Rappel téléphonique gratuit : 'Mon compte > Support > Demander un rappel'. Créneaux disponibles : 30 min en avance. Rappel garanti dans le créneau choisi pendant les heures d'ouverture."},

        // ── Problèmes techniques ─────────────────────────────────────────────
        {"bug",          "Pour signaler un bug : navigateur + version, URL exacte de la page, étapes pour reproduire, capture d'écran. Envoyez à bugs@toolbox.fr. Traitement prioritaire si bug bloquant (blocage d'achat)."},
        {"erreur",       "Codes d'erreur courants : E001 = session expirée (reconnectez-vous), E002 = rupture de stock (rechargez la page), E003 = paiement refusé (voir carte bancaire), E999 = erreur serveur (réessayez dans 15 min)."},
        {"probleme",     "Dépannage rapide : 1) Videz le cache (Ctrl+Shift+Del). 2) Désactivez les extensions. 3) Testez en navigation privée. 4) Essayez un autre navigateur. Si persistant, contactez le support avec une capture d'écran."},
        {"site",         "Notre site est monitoré 24h/24. Disponibilité : 99,9 % sur les 12 derniers mois. En cas de panne confirmée, un bandeau d'alerte apparaît et le statut est mis à jour sur status.toolbox.fr."},
        {"application",  "App disponible sur iOS 14+ et Android 8+. Fonctionnalités identiques au site web + notifications push pour le suivi de commande. Mise à jour automatique recommandée pour la sécurité."},
        {"cache",        "Vider le cache résout 80 % des problèmes d'affichage. Chrome/Edge : Ctrl+Shift+Del. Firefox : Ctrl+Shift+Del. Safari : Cmd+Option+E. Sur mobile : Paramètres > Applications > Cache."},
        {"compatible",   "Navigateurs supportés : Chrome 90+, Firefox 88+, Safari 14+, Edge 90+. Internet Explorer non supporté. Résolution minimale recommandée : 1280×720. JavaScript doit être activé."},

        // ── Données & sécurité ───────────────────────────────────────────────
        {"securite",     "Votre compte est protégé par : chiffrement SSL TLS 1.3, hachage bcrypt des mots de passe, 2FA optionnel, blocage après 5 tentatives échouées, détection d'activité suspecte avec alerte email."},
        {"fraude",       "Vous suspectez une fraude ? 1) Changez votre mot de passe immédiatement. 2) Activez le 2FA. 3) Vérifiez vos commandes récentes. 4) Appelez le 01 23 45 67 00 (urgence 24h/24). 5) Prévenez votre banque."},
        {"cookies",      "Cookies utilisés : essentiels (session, panier), analytiques (Matomo anonymisé), et marketing (opt-in requis). Gestion depuis le bandeau cookies ou 'Mon compte > Confidentialité'."},
        {"rgpd",         "Conformité RGPD : DPO joignable à dpo@toolbox.fr. Droits : accès, rectification, effacement, portabilité, opposition. Réponse sous 30 jours. Réclamation possible auprès de la CNIL."},

        // ── Fidélité & parrainage ─────────────────────────────────────────────
        {"fidelite",     "Programme fidélité : 1 point par euro dépensé. 100 points = 1 € de réduction. Bronze (200 pts) : 5 % de remise. Argent (500 pts) : 10 %. Or (1000 pts) : 15 % + livraison gratuite. Points valables 12 mois."},
        {"parrainage",   "Parrainez un ami : vous recevez 10 € de crédit quand il passe sa première commande ≥ 30 €. Votre ami reçoit -5 € sur sa première commande. Lien unique dans 'Mon compte > Parrainage'."},
        {"point",        "Vos points fidélité : consultables dans 'Mon compte > Fidélité'. Gagnés sur chaque commande livrée (non sur les remises). Utilisables dès 500 points. Expiration 12 mois après acquisition."},

        // ── Questions générales & small talk ────────────────────────────────
        {"bonjour",      "Bonjour ! Je suis l'assistant virtuel de la Toolbox. Je peux répondre à vos questions sur les commandes, paiements, livraisons, retours, votre compte et bien plus. Que puis-je faire pour vous ?"},
        {"salut",        "Salut ! Comment je peux vous aider aujourd'hui ? Commandes, livraisons, paiements, retours, compte, produits… n'hésitez pas à préciser votre besoin."},
        {"bonsoir",      "Bonsoir ! Je suis disponible 24h/24. Quelle est votre question ?"},
        {"aide",         "Je couvre : commandes, paiements, livraisons, retours, remboursements, compte, facturation, produits, support technique, fidélité, et données personnelles. Posez votre question directement."},
        {"help",         "Je peux vous aider avec : suivi commande, paiement refusé, retour article, remboursement, problème de livraison, mot de passe oublié, facture, bug technique. Décrivez votre problème."},
        {"merci",        "Avec plaisir ! N'hésitez pas si vous avez d'autres questions. Bonne journée !"},
        {"au revoir",    "Au revoir ! Si vous avez d'autres questions, je suis disponible 24h/24. Bonne journée !"},
        {"qui es-tu",    "Je suis l'assistant virtuel de la Data & IA Toolbox, disponible 24h/24. Je connais les commandes, livraisons, retours, paiements et bien plus. Pour les cas complexes, un conseiller humain peut prendre le relais."},
        {"que peux-tu",  "Je peux répondre sur : commandes & statuts, paiements & factures, livraisons & transporteurs, retours & remboursements, compte & sécurité, produits & stocks, programme fidélité, et support technique."},
        {"comment ca marche","Tapez votre question en français courant. Je détecte les mots-clés et vous fournis une réponse précise. Si ma réponse ne satisfait pas votre besoin, écrivez 'Conseiller humain' pour être transféré."},
    };
}
