/*
   Copyright 2009-2022 PrimeTek.

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.primefaces.poseidon.view;

import org.primefaces.poseidon.domain.Product;
import org.primefaces.poseidon.service.ProductService;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class DashboardView implements Serializable {

    private List<Product> productsThisWeek;

    private List<Product> productsLastWeek;

    private List<Product> products;

    private int productWeek = 0;

    @Inject
    private ProductService service;

    @PostConstruct
    public void init() {
        this.productsThisWeek = this.service.getProducts();
        this.productsLastWeek = this.service.getClonedProducts(25);
        this.products = this.productsThisWeek;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void loadProducts() {
        this.products = this.productWeek == 1 ? this.productsLastWeek : this.productsThisWeek;
    }

    public int getProductWeek() {
        return productWeek;
    }

    public void setProductWeek(int productWeek) {
        this.productWeek = productWeek;
    }
}
