# Raytracing Distribué

Un système de calcul distribué pour la génération d'images par lancer de rayons utilisant Java RMI.

## Description

Ce projet implémente un système de raytracing distribué qui permet de répartir le calcul d'une image sur plusieurs machines connectées en réseau. L'image est découpée en blocs de 128x128 pixels qui sont distribués équitablement entre les noeuds de calcul disponibles.


## Prérequis

- Java 8 ou supérieur
- Apache Ant (optionnel - pour la compilation automatisée)
- Réseau local ou connexion entre les machines

## Installation

### 1. Cloner le projet
```bash
git clone git@github.com:korban2u/DistRayTracer.git
cd DistRayTracer
```

### 2. Compiler le projet

#### Option A : Avec Apache Ant 
```bash
# Compiler tous les modules
ant build-all

# Ou compiler individuellement
ant build-server   # Serveur
ant build-noeud     # Noeud
ant build-client    # Client

# Nettoyer les fichiers compilés
ant clean
```

#### Option B : Avec javac (compilation manuelle)
```bash
# Créer les répertoires de sortie
mkdir -p bin/server bin/noeud bin/client

# Compiler le serveur
javac -d bin/server tracé_de_rayon/src/raytracer/*.java noeud/src/ServiceNoeud.java client/src/ServiceClient.java server/src/*.java

# Compiler les noeuds
javac -d bin/noeud tracé_de_rayon/src/raytracer/*.java server/src/ServiceDistributeur.java client/src/ServiceClient.java noeud/src/*.java

# Compiler le client
javac -d bin/client tracé_de_rayon/src/raytracer/*.java noeud/src/ServiceNoeud.java server/src/ServiceDistributeur.java client/src/*.java

# Copier les fichiers de description de scène
cp client/src/*.txt bin/client/
```


## Utilisation

### 1. Démarrer le registre RMI
```bash
# Sur la machine serveur
cd bin/server
rmiregistry [port]
```

### 2. Lancer le serveur distributeur
```bash
cd bin/server
java Serveur [port]
```
- `port` : port du registre RMI (défaut : 1099)

### 3. Lancer les nœuds de calcul
```bash
# Sur chaque machine de calcul
cd bin/noeud
java LancerNoeud [ip-serveur] [port]
```
- `ip-serveur` : adresse IP du serveur (défaut : localhost)
- `port` : port du serveur (défaut : 1099)

### 4. Lancer le client
```bash
cd bin/client
java LancerClient [ip-serveur] [port] [largeur] [hauteur] [fichier-scene]
```

**Paramètres :**
- `ip-serveur` : adresse IP du serveur (défaut : localhost)
- `port` : port du serveur (défaut : 1099)
- `largeur` : largeur de l'image (défaut : 512)
- `hauteur` : hauteur de l'image (défaut : 512, ou égale à largeur si non spécifiée)
- `fichier-scene` : fichier de description de la scène (défaut : simple.txt)

**Exemple :**
```bash
java LancerClient 192.168.1.100 1099 1024 768 simple.txt
```

## Modes de calcul

Au lancement du client, vous pouvez choisir entre deux modes :

### Mode Séquentiel
- Les blocs sont calculés un par un
- Plus lent mais plus stable

### Mode Parallèle (Threads)
- Tous les blocs sont calculés simultanément
- Plus rapide grâce à la parallélisation


## Structure du projet

```
raytracing-distribue/
├── client/src/          # Code du client
│   ├── Client.java
│   ├── LancerClient.java
│   ├── ClientCalculateurSequentiel.java
│   ├── ClientCalculateurThread.java
│   ├── ServiceClient.java
│   └── simple.txt       # Scène d'exemple
├── server/src/          # Code du serveur
│   ├── Serveur.java
│   ├── Distributeur.java
│   └── ServiceDistributeur.java
├── noeud/src/           # Code des noeuds
│   ├── LancerNoeud.java
│   ├── Noeud.java
│   └── ServiceNoeud.java
├── tracé_de_rayon/src/  # raytracing
├── bin/                 # Fichiers compilés
├── build.xml            # Script de compilation Ant
└── .gitignore
```


## Exemples d'utilisation

### Configuration locale (test)
```bash
# Terminal 1 : Registre RMI
cd bin/server
rmiregistry 1099

# Terminal 2 : Serveur
cd bin/server
java Serveur

# Terminal 3 : Nœud local
cd bin/noeud
java LancerNoeud

# Terminal 4 : Client
cd bin/client && java LancerClient
```



### Configuration avec des vraies machines
```bash
# Sur le serveur (192.168.1.100)
cd bin/server
rmiregistry 1099
java Serveur

# Sur chaque machine de calcul
cd bin/noeud
java LancerNoeud 192.168.1.100 1099

# Sur le client
cd bin/client
java LancerClient 192.168.1.100 1099 1024 1024
```

## Architecture

Le système est composé de trois modules principaux :

### **Serveur (Distributeur)**
- Gère l'enregistrement des nœuds de calcul
- Maintient la liste des nœuds disponibles
- Détecte et supprime les nœuds déconnectés
- Utilise le registre RMI pour la communication

### **Nœud (Machine de calcul)**
- Effectue les calculs de raytracing pour des blocs d'image
- S'enregistre automatiquement auprès du distributeur
- Reçoit la scène à calculer du client
- Peut être lancé sur plusieurs machines

### **Client**
- Interface utilisateur pour lancer le calcul
- Découpe l'image en blocs et les distribue
- Reçoit et affiche les résultats en temps réel
- Propose deux modes : séquentiel et parallèle (threads)

## Groupe

Ryan Korban, Maxence Eva, Baptiste Henequin, Baptiste Delaborde
