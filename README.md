Progetto PR2 – Anno Accademico 2013/2014
=========

###Componenti del team
* Simone Cusimano - 47923
* Giancarlo Lelli - 47792


Descrizione del progetto
------------------------
Tramite il seguente progetto è stata sviluppata un estensione del motore di querying del progetto open source **Apache Jena**. La suddetta estensione si interfaccia con i servizi di **URL shortening di Google**, supportando sia il caso di accorciamento di un indirizzo, sia quello dell’esplosione di un indirizzo già accorciato. 


Configurazione dell’estensione
------------------------------
Una volta scompattato l’archivio **ProjectRTM.zip** ci si ritroverà davanti a un file con estensione .jar ed a una cartella di chiamata **ProjectRTM**, per procedere al test del progetto bisogna posizionare la cartella nella root dell’installazione di Apache Jena e successivamente copiare all’interno della cartella **/lib**, contenuta nella root di Apache Jena, il file **pr2project.jar**. Una volta terminati questi step è possibile procedere con il test vero e proprio.


Utilizzo dell’estensione
------------------------
Per utilizzare l’estensione recarsi all’interno della cartella **ProjectRTM** precedentemente copiata all’interno della root di Apache Jena. Una volta dentro eseguire (qualora non lo si fosse mai fatto) i seguenti comandi per registrare la variabile d’ambiente JENAROOT all’interno del proprio sistema operativo. 

###Comandi per i sistemi Unix
```sh
export JENAROOT=/usr/local/apache-jena
PATH=$JENAROOT/bin:$PATH
```
###Comandi per i sistemi Windows
```sh
SET JENAROOT=\Users\somebody\dev\apache-jena
SET PATH=%PATH%;%JENAROOT\bin
```

Ovviamente i path variano a seconda di dove si è deciso di scompattare l’archivio. Una volta che si è sicuri che i comandi precedentemente digitati siano andati a buon fine, per testare l’estensione è sufficiente eseguire il seguente comando:

###Comandi per i sistemi Unix
```sh
../bin/sparql --data=db.rdf --query=query.rq
```
###Comandi per i sistemi Windows
```sh
..\bat\sparql.bat --data=db.rdf --query=query.rq
```


Funzionamento dell’estensione
-----------------------------
La classe **JenaExtension** svolge le sue funzioni grazie alle API di URL Shortening offerte da Google. L’interfacciamento con tale servizio avviene per mezzo di chiamate http, in POST qualora volessimo accorciare un indirizzo e in GET qualora volessimo esplodere un indirizzo precedentemente accorciato. La gestione della risposta dal servizio avviene mediante una libreria esterna (già contenuta in ApacheJena) che deserializza il JSON di risposta in un oggetto di tipo Map<String, Object>().  

Viene fatto un forte filtraggio degli input non correttamente formattati per evitare di eseguire delle richieste “a vuoto” al web service, tali controlli si basano fondamentalmente sulla natura della stringa ricevuta in input. In particolare è considerata valida una qualsiasi stringa che inizia per **http://** o **https://** qualora si stesse facendo una richiesta di shortening, mentre invece qualora stessimo facendo una richiesta di esplosione è considerato valido un qualiasi URL che abbia come hostname **goo.gl**.


Progettazione dell’estensione
-----------------------------
La classe eredita da una superclasse chiamata **PFuncAssignToObject** che mette a disposizione un metodo astratto di nome ```calc``` che accetta come parametro un solo oggetto di tipo Node. E’ dentro questo metodo che vengono effettuati i primi controlli sull’input e successivamente in base alla natura dell’input stesso viene determinata l’eventuale azione da compiere.


Spiegazione dell'ambiente di testing
------------------------------------------------------
Come esempio, insieme all’estensione viene fornito un file RDF contenente una serie di informazioni codificate secondo appunto lo standard RDF; in particolare, in questo caso le entries di questo database di prova simulano delle vCard che riportano un campo contenente il nome di un sito web e un altro campo contenente il loro indirizzo esteso. Un esempio è riportato in basso:

```sh
<rdf:Description rdf:about="http://www.google.com">
    <vCard:FN>Google</vCard:FN>
</rdf:Description>
```
Lo script di querying invece è riportato di seguito:
```sh
PREFIX ex: <java:it.unica.pr2.>

SELECT ?OriginalURL ?ShortenedURL ?ConvertedBackURL
{ 
	?OriginalURL <http://www.w3.org/2001/vcard-rdf/3.0#FN> ?fullSelector.
	?OriginalURL ex:JenaExtension ?ShortenedURL .
	?ShortenedURL ex:JenaExtension ?ConvertedBackURL
}
```

La sua interpretazione è piuttosto semplice. La prima riga registra in maniera dinamica (grazie al prefisso java: ) la nostra estensione all’interno del **Query Engine**, e ne abbrevia il namespace/package name permettendoci di indicare semplicemente ex. Successivamente, tramite il costrutto **SELECT** andiamo a selezionare tre campi/variabili: **?ShortenedURL ?OriginalURL ?ConvertedBackURL** (i nomi sono inventati in maniera da rispecchiare il valore del dato che conterranno). Fatto questo, inizia la vera e propria elaborazione; la prima riga contenuta dentro il Select è responsabile del reperimento del valore contenuto nel campo **rdf:about** del file, una volta prelevato lo memorizza nel campo **?OriginalURL**. La seconda riga, invece, calcola, tramite il valore appena computato, lo short URL di quella risorsa e lo memorizza nella variabile ?ShortenedURL; infine, la terza riga ri-converte l’url accorciato in url esteso, memorizzando il risultato nella variabile ?ConvertedBackURL.


Licenza
-------
This license governs use of the accompanying software. If you use the software, you
accept this license. If you do not accept the license, do not use the software.

1. ###Definitions
The terms "reproduce," "reproduction," "derivative works," and "distribution" have the
same meaning here as under U.S. copyright law.
A "contribution" is the original software, or any additions or changes to the software.
A "contributor" is any person that distributes its contribution under this license.
"Licensed patents" are a contributor's patent claims that read directly on its contribution.

2. ###Grant of Rights
* **Copyright Grant** - Subject to the terms of this license, including the license conditions and limitations in section 3, each contributor grants you a non-exclusive, worldwide, royalty-free copyright license to reproduce its contribution, prepare derivative works of its contribution, and distribute its contribution or any derivative works that you create.
(B) Patent Grant- Subject to the terms of this license, including the license conditions and limitations in section 3, each contributor grants you a non-exclusive, worldwide, royalty-free license under its licensed patents to make, have made, use, sell, offer for sale, import, and/or otherwise dispose of its contribution in the software or derivative works of the contribution in the software.

3. ###Conditions and Limitations
* **No Trademark License** - This license does not grant you rights to use any contributors' name, logo, or trademarks.
* If you bring a patent claim against any contributor over patents that you claim are infringed by the software, your patent license from such contributor to the software ends automatically.
* If you distribute any portion of the software, you must retain all copyright, patent, trademark, and attribution notices that are present in the software.
* If you distribute any portion of the software in source code form, you may do so only under this license by including a complete copy of this license with your distribution. If you distribute any portion of the software in compiled or object code form, you may only do so under a license that complies with this license.
* The software is licensed "as-is." You bear the risk of using it. The contributors give no express warranties, guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot change. To the extent permitted under your local laws, the contributors exclude the implied warranties of merchantability, fitness for a particular purpose and non-infringement.