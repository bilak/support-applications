# ZIP Code crawler service

Used to grab zip codes from providers and expose them as simple rest service which can be queried.

## Slovak ZIP Code crawler
Retrieves all ZIP codes (in slovak language PŠČ - poštové smerovacie číslo) from zip archive provided at 
[slovak post](https://www.posta.sk/sluzby/postove-smerovacie-cisla). Only files `ULICE.xlsx` and `OBCE.xlsx` are processed.  
Synchronization is performed by cron defined with property `zip-code-crawler.sk-download-cron` (default values is `0 0 23 ? * SAT` each 
saturday at 23:00).  
ZIP codes are stored to H2 database ase they are, no transformation or reparation is performed.


There are two rest endpoints which could be used for querying resources. Attributes are queried as *lowercase* with *contains* condition. So for 
example if exists entry with `name=This is test name` and query is specified with `name=IS TEST` then this record should be returned.
  
Endpoints urls: 
* `[GET] <server_host:port>/slovak/cities` with attributes of 
[city](./src/main/java/com/github/bilak/zipcodes/slovak/persistence/model/Obec.java)
* `[GET] <server_host:port>/slovak/streets` with attributes of 
[street](./src/main/java/com/github/bilak/zipcodes/slovak/persistence/model/Ulica.java)