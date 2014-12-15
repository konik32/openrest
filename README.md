openrest
========
	<section class="col-sm-12">
		<article>
			<h3>Podstawowe informacje</h3>
			<p>
				Rozszerzenie zadziała tylko jeśli do zapytania dodamy parameter
				<code class="prettyprint prettyprinted"><span class="pln">orest</span></code>
				.
			</p>
		</article>
		<article>
			<h3>Podstawowe filtrowanie</h3>
			<h5>Format:</h5>
			<p>
				Funkcje
				<code class="prettyprint prettyprinted"><span class="pln">function_name</span><span class="pun">(</span><span class="pln">resource_property_path</span><span class="pun">,</span><span class="pln"> resource_property_values</span><span class="pun">...)</span></code>
				rozdzielone
				<code class="prettyprint prettyprinted"><span class="pun">;</span><span class="kwd">and</span><span class="pun">;</span></code>
				<code class="prettyprint prettyprinted"><span class="pun">;</span><span class="kwd">or</span><span class="pun">;</span></code>
				<br> Istnieje również możliwość zagnieżdżania warunków z użyciem
				nawiasów.
			</p>
			<h5>Nazwy funkcji:</h5>
			<code class="prettyprint prettyprinted"><span class="pln">between</span><span class="pun">,</span><span class="pln"> isNotNull</span><span class="pun">,</span><span class="pln"> isNull</span><span class="pun">,</span><span class="pln"> lt</span><span class="pun">,</span><span class="pln"> gt</span><span class="pun">,</span><span class="pln"> ge</span><span class="pun">,</span><span class="pln"> le</span><span class="pun">,</span><span class="pln"> before</span><span class="pun">,</span><span class="pln"> after</span><span class="pun">,</span><span class="pln"> notLike</span><span class="pun">,</span><span class="pln"> like</span><span class="pun">,</span><span class="pln"> statingWith</span><span class="pun">,</span><span class="pln"> endingWith</span><span class="pun">,</span><span class="pln"> containing</span><span class="pun">,</span><span class="pln"> notIn</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">in</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">true</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">false</span><span class="pun">,</span><span class="pln"> eq</span><span class="pun">,</span><span class="pln"> notEq</span></code>

			<h5>Format URI:</h5>
			<code class="prettyprint prettyprinted"><span class="str">/resource/</span><span class="pln">id</span><span class="pun">/</span><span class="kwd">property</span></code>

			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="podst_filt_url">http://localhost:8080/api/products?orest&amp;filter=between(price,600,1500);and;eq(state,'BRAND_NEW');or;between(price,100,600);and;eq(state,'NEW')&amp;size=2&amp;page=0</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="podst_filt">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products?orest&amp;filter=between(price,600,1500);and;eq(state,'BRAND_NEW');or;between(price,100,600);and;eq(state,'NEW')&amp;size=2&amp;page=0{&amp;expand,sFilter,distinct,dto,count,sort}",
      "templated": true
    },
    "next": {
      "href": "http://localhost:8080/api/products?orest&amp;filter=between(price,600,1500);and;eq(state,'BRAND_NEW');or;between(price,100,600);and;eq(state,'NEW')&amp;page=1&amp;size=2{&amp;expand,sFilter,distinct,dto,count,sort}",
      "templated": true
    }
  },
  "_embedded": {
    "products": [
      {
        "name": "LG",
        "price": 200,
        "location": "POLAND",
        "state": "NEW",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/1{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/1/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/1/shop"
          }
        }
      },
      {
        "name": "Samsung",
        "price": 670,
        "location": "EUROPE",
        "state": "BRAND_NEW",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/2{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/2/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/2/shop"
          }
        }
      }
    ]
  },
  "page": {
    "size": 2,
    "totalElements": 7,
    "totalPages": 4,
    "number": 0
  }
}</pre>
		</article>
		<article>
			<h3>Rozszerzanie</h3>
			<p>
				Rozszerzamy poprzez użycie parametru
				<code class="prettyprint prettyprinted"><span class="pln">expand</span></code>
				. Nie można rozszerzać obiektów typu
				<code class="prettyprint prettyprinted"><span class="typ">Collection</span></code>
				.
			</p>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="roz_filt_url">http://localhost:8080/api/products/1?orest&amp;expand=shop.user.address</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="roz_filt">{
  "name": "LG",
  "price": 200,
  "location": "POLAND",
  "state": "NEW",
  "new": false,
  "_embedded": {
    "shop": {
      "name": "GSM",
      "new": false,
      "_embedded": {
        "user": {
          "username": "tomasz",
          "password": "password",
          "new": false,
          "_embedded": {
            "address": {
              "city": "Kraków",
              "homeNr": "123",
              "street": "Krakowska",
              "zip": "33-100",
              "new": false,
              "_links": {
                "self": {
                  "href": "http://localhost:8080/api/addresses/1{?orest,expand,sFilter,distinct,dto}",
                  "templated": true
                }
              }
            }
          },
          "_links": {
            "self": {
              "href": "http://localhost:8080/api/users/1{?orest,expand,sFilter,distinct,dto}",
              "templated": true
            },
            "address": {
              "href": "http://localhost:8080/api/users/1/address"
            },
            "shops": {
              "href": "http://localhost:8080/api/users/1/shops"
            }
          }
        }
      },
      "_links": {
        "self": {
          "href": "http://localhost:8080/api/shops/1{?orest,expand,sFilter,distinct,dto}",
          "templated": true
        },
        "products": {
          "href": "http://localhost:8080/api/shops/1/products"
        },
        "user": {
          "href": "http://localhost:8080/api/shops/1/user"
        },
        "addresses": {
          "href": "http://localhost:8080/api/shops/1/addresses"
        }
      }
    }
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products/1{?orest,expand,sFilter,distinct,dto}",
      "templated": true
    },
    "category": {
      "href": "http://localhost:8080/api/products/1/category"
    },
    "shop": {
      "href": "http://localhost:8080/api/products/1/shop"
    }
  }
}</pre>
		</article>
		<article>
			<h3>Statyczny filtr</h3>
			<p>
				Jeśli dodamy adnotacje
				<code class="prettyprint prettyprinted"><span class="lit">@StaticFilter</span></code>
				na jakąś encje filtr ten będzie dodawany do każdego zapytania o tą
				encję
			</p>
			<pre class="prettyprint lang-java prettyprinted"><span class="lit">@StaticFilter</span><span class="pun">(</span><span class="pln">value</span><span class="pun">=</span><span class="str">"eq(state,'NEW');or;eq(state,'BRAND_NEW')"</span><span class="pun">,</span><span class="pln"> name</span><span class="pun">=</span><span class="str">"new_products"</span><span class="pun">)</span></pre>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="static_filt_url">http://localhost:8080/api/products?orest&amp;filter=eq(state,'OLD')</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="static_filt">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products?orest&amp;filter=eq(state,'OLD'){&amp;expand,sFilter,distinct,dto,count,page,size,sort}",
      "templated": true
    }
  },
  "page": {
    "size": 20,
    "totalElements": 0,
    "totalPages": 0,
    "number": 0
  }
}</pre>

			<h3>Statyczny filtr wyłączony</h3>
			<p>
				Statyczne filtry możemy wyłączyć poprzez wymienienie ich w
				parametrze
				<code class="prettyprint prettyprinted"><span class="pln">sFilter</span></code>
			</p>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="wyl_static_filt_url">http://localhost:8080/api/products?orest&amp;filter=eq(state,'OLD')&amp;sFilter=new_products</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="wyl_static_filt">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products?orest&amp;filter=eq(state,'OLD')&amp;sFilter=new_products{&amp;expand,distinct,dto,count,page,size,sort}",
      "templated": true
    }
  },
  "_embedded": {
    "products": [
      {
        "name": "Iphone",
        "price": 1200,
        "location": "WORLD",
        "state": "OLD",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/3{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/3/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/3/shop"
          }
        }
      },
      {
        "name": "Nokia",
        "price": 20,
        "location": "POLAND",
        "state": "OLD",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/6{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/6/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/6/shop"
          }
        }
      },
      {
        "name": "GTA",
        "price": 1200,
        "location": "WORLD",
        "state": "OLD",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/9{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/9/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/9/shop"
          }
        }
      },
      {
        "name": "PES",
        "price": 20,
        "location": "POLAND",
        "state": "OLD",
        "new": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/products/12{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "category": {
            "href": "http://localhost:8080/api/products/12/category"
          },
          "shop": {
            "href": "http://localhost:8080/api/products/12/shop"
          }
        }
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 4,
    "totalPages": 1,
    "number": 0
  }
}</pre>
		</article>
		<article>
			<h3>DTO</h3>
			<p>
				Do dowolnego zasobu znajującego się w odpowiedzi możemy dołączyć
				jakikolwiek obiekt. W tym celu należy stworzyć klasę z adnotacją
				<code class="prettyprint lang-java prettyprinted"><span class="lit">@RepositoryEventHandler</span></code>
				, oraz metodą z adnotacją
				<code class="prettyprint lang-java prettyprinted"><span class="lit">@DtoPopulatorHandler</span></code>
				adnotacja przyjmuje dwa parametry:
				<code class="prettyprint lang-java prettyprinted"><span class="pln">value</span></code>
				- klasa obiektu, dla którego chcemy wywołać metodę oraz
				<code class="prettyprint prettyprinted"><span class="pln">dtos</span></code>
				. Metoda musi zwracać obiekt typu
				<code class="prettyprint lang-java prettyprinted"><span class="typ">List</span><span class="pun">&lt;</span><span class="typ">EmbeddedWrapper</span><span class="pun">&gt;</span></code>
				oraz przyjmować parametry: Zasób, dto. Do tworzenia obiektów typu
				<code class="prettyprint lang-java prettyprinted"><span class="typ">EmbeddedWrapper</span></code>
				można użyć beana
				<code class="prettyprint lang-java prettyprinted"><span class="typ">EmbeddedWrapperFactory</span></code>
				<br> Przkład:
			</p><pre class="prettyprint lang-java prettyprinted"><span class="lit">@RepositoryEventHandler</span><span class="pln">
</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">class</span><span class="pln"> </span><span class="typ">DtoHandler</span><span class="pln"> </span><span class="pun">{</span><span class="pln">

	</span><span class="lit">@Autowired</span><span class="pln">
	</span><span class="kwd">private</span><span class="pln"> </span><span class="typ">EmbeddedWrapperFactory</span><span class="pln"> wrappers</span><span class="pun">;</span><span class="pln">

	</span><span class="lit">@DtoPopulatorHandler</span><span class="pun">(</span><span class="pln">value </span><span class="pun">=</span><span class="pln"> </span><span class="typ">User</span><span class="pun">.</span><span class="kwd">class</span><span class="pun">,</span><span class="pln"> dtos </span><span class="pun">=</span><span class="pln"> </span><span class="str">"details"</span><span class="pun">)</span><span class="pln">
	</span><span class="kwd">public</span><span class="pln"> </span><span class="typ">List</span><span class="pun">&lt;</span><span class="typ">EmbeddedWrapper</span><span class="pun">&gt;</span><span class="pln"> populateUserDto</span><span class="pun">(</span><span class="typ">User</span><span class="pln"> user</span><span class="pun">,</span><span class="pln"> </span><span class="typ">String</span><span class="pln"> dto</span><span class="pun">)</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
		</span><span class="typ">Map</span><span class="pun">&lt;</span><span class="typ">String</span><span class="pln"> </span><span class="pun">,</span><span class="pln"> </span><span class="typ">String</span><span class="pun">&gt;</span><span class="pln"> weather </span><span class="pun">=</span><span class="pln"> </span><span class="kwd">new</span><span class="pln"> </span><span class="typ">HashMap</span><span class="pun">&lt;</span><span class="typ">String</span><span class="pln"> </span><span class="pun">,</span><span class="pln"> </span><span class="typ">String</span><span class="pun">&gt;();</span><span class="pln">
		weather</span><span class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span class="str">"origin"</span><span class="pun">,</span><span class="pln"> </span><span class="str">"Imię "</span><span class="pln"> </span><span class="pun">+</span><span class="pln"> user</span><span class="pun">.</span><span class="pln">getUsername</span><span class="pun">()</span><span class="pln"> </span><span class="pun">+</span><span class="pln"> </span><span class="str">" wywodzi się z mitologi jakiejś tam"</span><span class="pun">);</span><span class="pln">
		weather</span><span class="pun">.</span><span class="pln">put</span><span class="pun">(</span><span class="pln">
				</span><span class="str">"details"</span><span class="pun">,</span><span class="pln">
				</span><span class="str">"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"</span><span class="pun">);</span><span class="pln">
		</span><span class="kwd">return</span><span class="pln"> </span><span class="typ">Arrays</span><span class="pun">.</span><span class="pln">asList</span><span class="pun">(</span><span class="pln">wrappers</span><span class="pun">.</span><span class="pln">wrap</span><span class="pun">(</span><span class="pln">weather</span><span class="pun">,</span><span class="pln"> </span><span class="str">"nameDetails"</span><span class="pun">,</span><span class="pln"> </span><span class="kwd">false</span><span class="pun">));</span><span class="pln">
	</span><span class="pun">}</span><span class="pln">
</span><span class="pun">}</span><span class="pln">
			</span></pre>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="dto_url">http://localhost:8080/api/shops?orest&amp;expand=user&amp;dto=details</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="dto">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/shops?orest&amp;expand=user&amp;dto=details{&amp;sFilter,distinct,filter,count,page,size,sort}",
      "templated": true
    }
  },
  "_embedded": {
    "shops": [
      {
        "name": "GSM",
        "new": false,
        "_embedded": {
          "user": {
            "username": "tomasz",
            "password": "password",
            "new": false,
            "_embedded": {
              "nameDetails": {
                "origin": "Imię tomasz wywodzi się z mitologi jakiejś tam",
                "details": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
              }
            },
            "_links": {
              "self": {
                "href": "http://localhost:8080/api/users/1{?orest,expand,sFilter,distinct,dto}",
                "templated": true
              },
              "address": {
                "href": "http://localhost:8080/api/users/1/address"
              },
              "shops": {
                "href": "http://localhost:8080/api/users/1/shops"
              }
            }
          }
        },
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/shops/1{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "products": {
            "href": "http://localhost:8080/api/shops/1/products"
          },
          "user": {
            "href": "http://localhost:8080/api/shops/1/user"
          },
          "addresses": {
            "href": "http://localhost:8080/api/shops/1/addresses"
          }
        }
      },
      {
        "name": "Gry",
        "new": false,
        "_embedded": {
          "user": {
            "username": "bartek",
            "password": "password",
            "new": false,
            "_embedded": {
              "nameDetails": {
                "origin": "Imię bartek wywodzi się z mitologi jakiejś tam",
                "details": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
              }
            },
            "_links": {
              "self": {
                "href": "http://localhost:8080/api/users/2{?orest,expand,sFilter,distinct,dto}",
                "templated": true
              },
              "address": {
                "href": "http://localhost:8080/api/users/2/address"
              },
              "shops": {
                "href": "http://localhost:8080/api/users/2/shops"
              }
            }
          }
        },
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/shops/2{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "products": {
            "href": "http://localhost:8080/api/shops/2/products"
          },
          "user": {
            "href": "http://localhost:8080/api/shops/2/user"
          },
          "addresses": {
            "href": "http://localhost:8080/api/shops/2/addresses"
          }
        }
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  }
}</pre>
		</article>
		<article>
			<h3>Eventy</h3>
			<p>
				Przed i po wywołaniu zapytania w bazie tworzone są eventy, które
				można obsłużyć. W tym celu tworzymy klasę z adnotacją
				<code class="prettyprint lang-java prettyprinted"><span class="lit">@RepositoryEventHandler</span></code>
				, oraz metodami z adnotacjami
				<code class="prettyprint lang-java prettyprinted"><span class="lit">@HandleBeforeCollectionGet</span><span class="pln"> </span><span class="lit">@HandleBeforeGet</span><span class="pln"> </span><span class="lit">@HandleAfterCollectionGet</span><span class="pln"> </span><span class="lit">@HandleAfterGet</span></code>
				adnotacja przyjmuje parametr
				<code class="prettyprint lang-java prettyprinted"><span class="pln">value</span></code>
				- klasa obiektu, dla którego chcemy wywołać metodę. <br> Przkład:
			</p>
			<pre class="prettyprint lang-java prettyprinted"><span class="lit">@RepositoryEventHandler</span><span class="pln">
</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">class</span><span class="pln"> </span><span class="typ">EventHandler</span><span class="pln"> </span><span class="pun">{</span><span class="pln">

	</span><span class="lit">@HandleBeforeCollectionGet</span><span class="pun">(</span><span class="typ">User</span><span class="pun">.</span><span class="kwd">class</span><span class="pun">)</span><span class="pln">
	</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">void</span><span class="pln"> onBeforeCollection</span><span class="pun">(</span><span class="typ">ParsedRequest</span><span class="pln"> request</span><span class="pun">)</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
		</span><span class="typ">System</span><span class="pun">.</span><span class="pln">out</span><span class="pun">.</span><span class="pln">println</span><span class="pun">(</span><span class="pln">request</span><span class="pun">.</span><span class="pln">getDomainClass</span><span class="pun">());</span><span class="pln">
	</span><span class="pun">}</span><span class="pln">

	</span><span class="lit">@HandleBeforeGet</span><span class="pun">(</span><span class="typ">User</span><span class="pun">.</span><span class="kwd">class</span><span class="pun">)</span><span class="pln">
	</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">void</span><span class="pln"> onBefore</span><span class="pun">(</span><span class="typ">ParsedRequest</span><span class="pln"> request</span><span class="pun">)</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
		</span><span class="typ">System</span><span class="pun">.</span><span class="pln">out</span><span class="pun">.</span><span class="pln">println</span><span class="pun">(</span><span class="pln">request</span><span class="pun">.</span><span class="pln">getDomainClass</span><span class="pun">());</span><span class="pln">
	</span><span class="pun">}</span><span class="pln">

	</span><span class="lit">@HandleAfterCollectionGet</span><span class="pun">(</span><span class="typ">User</span><span class="pun">.</span><span class="kwd">class</span><span class="pun">)</span><span class="pln">
	</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">void</span><span class="pln"> onAfterCollection</span><span class="pun">(</span><span class="typ">PagedResources</span><span class="pln"> resource</span><span class="pun">)</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
		</span><span class="typ">System</span><span class="pun">.</span><span class="pln">out</span><span class="pun">.</span><span class="pln">println</span><span class="pun">(</span><span class="pln">resource</span><span class="pun">.</span><span class="pln">getMetadata</span><span class="pun">().</span><span class="pln">getSize</span><span class="pun">());</span><span class="pln">
	</span><span class="pun">}</span><span class="pln">

	</span><span class="lit">@HandleAfterGet</span><span class="pun">(</span><span class="typ">User</span><span class="pun">.</span><span class="kwd">class</span><span class="pun">)</span><span class="pln">
	</span><span class="kwd">public</span><span class="pln"> </span><span class="kwd">void</span><span class="pln"> onAfterCollection</span><span class="pun">(</span><span class="typ">PersistentEntityResource</span><span class="pln"> resource</span><span class="pun">)</span><span class="pln"> </span><span class="pun">{</span><span class="pln">
		</span><span class="typ">System</span><span class="pun">.</span><span class="pln">out</span><span class="pun">.</span><span class="pln">println</span><span class="pun">(</span><span class="pln">resource</span><span class="pun">.</span><span class="pln">getContent</span><span class="pun">());</span><span class="pln">
	</span><span class="pun">}</span><span class="pln">

</span><span class="pun">}</span><span class="pln">
			</span></pre>
		</article>

		<h2>Autoryzacja</h2>
		<article>


			<h3>Użycie eventów</h3>
			<p>
				Parametry przekazywane do eventów zawierają wszelkie informacje o
				zapytaniu oraz odpowiedzi. Jeśli użytkownik nie będzie miał praw do
				danych zasobów wystarczy wyrzucić wyjątek
				<code class="prettyprint lang-java prettyprinted"><span class="typ">NotAuthorizedException</span></code>
			</p>
		</article>
		<article>
			<h3>ResourceFilter</h3>
			<p>
				Odpowiedź zwracana przez OPENREST to drzewo zasobów typu
				<code class="prettyprint lang-java prettyprinted"><span class="typ">Resource</span></code>
				zawierających wiele niepotrzenych do autoryzacji danych. Dlatego
				możemy przeglądnąć to drzewo jako encje z odniesieniami do rodziców.
				Wystarczy zaimplementować interfejs
				<code class="prettyprint lang-java prettyprinted"><span class="typ">ResourceFilter</span></code>
			</p><pre class="prettyprint lang-java prettyprinted"><span class="kwd">public</span><span class="pln"> </span><span class="kwd">interface</span><span class="pln"> </span><span class="typ">ResourceFilter</span><span class="pun">&lt;</span><span class="pln">T</span><span class="pun">&gt;</span><span class="pln"> </span><span class="pun">{</span><span class="pln">

	</span><span class="kwd">void</span><span class="pln"> validateMainResource</span><span class="pun">(</span><span class="typ">HttpServletRequest</span><span class="pln"> request</span><span class="pun">,</span><span class="pln"> T resource</span><span class="pun">)</span><span class="pln"> </span><span class="kwd">throws</span><span class="pln"> </span><span class="typ">NotAuthorizedException</span><span class="pun">;</span><span class="pln">

	</span><span class="kwd">boolean</span><span class="pln"> validateResource</span><span class="pun">(</span><span class="typ">HttpServletRequest</span><span class="pln"> request</span><span class="pun">,</span><span class="pln"> T resource</span><span class="pun">,</span><span class="pln"> </span><span class="typ">ParentAwareObject</span><span class="pln"> parent</span><span class="pun">);</span><span class="pln">

	</span><span class="typ">Class</span><span class="pun">&lt;?&gt;</span><span class="pln"> supports</span><span class="pun">();</span><span class="pln">

</span><span class="pun">}</span><span class="pln">
			</span></pre>
		</article>
		<article>
			<h3>Filtrowanie pól</h3>
			<p>
				Możemy zdecydować, które atrybuty danej encji zwracamy w odpowiedzi.
				Możliwości mamy dwie. Dodanie adnotacji
				<code class="prettyprint prettyprinted"><span class="lit">@SpelFilter</span></code>
				, która sprawdza warunek
				<code class="prettyprint prettyprinted"><span class="pln">value</span></code>
				na podstawie kontekstu(obiekt zwracany oraz obiekt typu
				<code class="prettyprint prettyprinted"><span class="typ">HttpServletRequest</span></code>
				). Jeśli nie jest on spełniony atrybuty wymienione w
				<code class="prettyprint prettyprinted"><span class="pln">properties</span></code>
				nie będą zwrócone w odpowiedzi. Na razie nie można odfiltrowywać
				assocjacji. Drugi sposób to stworzenie własnego filtra poprzez
				rozszerzenie klasy
				<code class="prettyprint prettyprinted"><span class="typ">AbstractContextFilter</span></code>
				, zarejestrowaniu go w
				<code class="prettyprint prettyprinted"><span class="typ">BoostFilterProvider</span></code>
				i dodaniu adnotacji
				<code class="prettyprint prettyprinted"><span class="typ">ContextFilter</span></code>
				z nazwą filtra.
			</p><pre class="prettyprint lang-java prettyprinted"><span class="lit">@SpelFilter</span><span class="pun">(</span><span class="pln">value</span><span class="pun">=</span><span class="str">"filteredObject.username == 'bartek'"</span><span class="pun">,</span><span class="pln"> properties</span><span class="pun">=</span><span class="str">"password"</span><span class="pun">)</span></pre>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="spel_filt_url">http://localhost:8080/api/users?orest&amp;filter=like(username,'tomasz')</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="spel_filt">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/users?orest&amp;filter=like(username,'tomasz'){&amp;expand,sFilter,distinct,dto,count,page,size,sort}",
      "templated": true
    }
  },
  "_embedded": {
    "users": [
      {
        "username": "tomasz",
        "new": false,
        "_embedded": {
          "shops": [
            {
              "rel": null,
              "value": {
                "name": "GSM",
                "new": false,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/shops/1{?orest,expand,sFilter,distinct,dto}",
                    "templated": true
                  },
                  "products": {
                    "href": "http://localhost:8080/api/shops/1/products"
                  },
                  "user": {
                    "href": "http://localhost:8080/api/shops/1/user"
                  },
                  "addresses": {
                    "href": "http://localhost:8080/api/shops/1/addresses"
                  }
                }
              },
              "collectionValue": false,
              "relTargetType": "example.shop.model.Shop"
            }
          ]
        },
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/users/1{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "address": {
            "href": "http://localhost:8080/api/users/1/address"
          },
          "shops": {
            "href": "http://localhost:8080/api/users/1/shops"
          }
        }
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}</pre>
			<h5>Zapytanie:</h5>
			<pre class="prettyprint prettyprinted" id="wyl_spel_filt_url">http://localhost:8080/api/users?orest&amp;filter=like(username,'bartek')</pre>
			<h5>Odpowiedź:</h5>
			<pre class="prettyprint lang-js prettyprinted" id="wyl_spel_filt">{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/users?orest&amp;filter=like(username,'bartek'){&amp;expand,sFilter,distinct,dto,count,page,size,sort}",
      "templated": true
    }
  },
  "_embedded": {
    "users": [
      {
        "username": "bartek",
        "password": "password",
        "new": false,
        "_embedded": {
          "shops": [
            {
              "rel": null,
              "value": {
                "name": "Gry",
                "new": false,
                "_links": {
                  "self": {
                    "href": "http://localhost:8080/api/shops/2{?orest,expand,sFilter,distinct,dto}",
                    "templated": true
                  },
                  "products": {
                    "href": "http://localhost:8080/api/shops/2/products"
                  },
                  "user": {
                    "href": "http://localhost:8080/api/shops/2/user"
                  },
                  "addresses": {
                    "href": "http://localhost:8080/api/shops/2/addresses"
                  }
                }
              },
              "collectionValue": false,
              "relTargetType": "example.shop.model.Shop"
            }
          ]
        },
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/users/2{?orest,expand,sFilter,distinct,dto}",
            "templated": true
          },
          "address": {
            "href": "http://localhost:8080/api/users/2/address"
          },
          "shops": {
            "href": "http://localhost:8080/api/users/2/shops"
          }
        }
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}</pre>
		</article>


	</section>
