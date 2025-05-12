/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.poseidon.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.primefaces.poseidon.domain.Photo;

@Named
@ApplicationScoped
public class PhotoService {

    private List<Photo> photos;

    @PostConstruct
    public void init() {
        photos = new ArrayList<>();

        photos.add(new Photo("demo/images/galleria/photo1.jpg", "demo/images/galleria/galleria1s.jpg",
                "Description for Image 1", "Title 1"));
        photos.add(new Photo("demo/images/galleria/photo2.jpg", "demo/images/galleria/galleria2s.jpg",
                "Description for Image 2", "Title 2"));
        photos.add(new Photo("demo/images/galleria/photo3.jpg", "demo/images/galleria/galleria3s.jpg",
                "Description for Image 3", "Title 3"));
        photos.add(new Photo("demo/images/galleria/photo4.jpg", "demo/images/galleria/galleria4s.jpg",
                "Description for Image 4", "Title 4"));
        photos.add(new Photo("demo/images/galleria/photo5.jpg", "demo/images/galleria/galleria5s.jpg",
                "Description for Image 5", "Title 5"));
        photos.add(new Photo("demo/images/galleria/photo6.jpg", "demo/images/galleria/galleria5s.jpg",
                "Description for Image 5", "Title 5"));
        photos.add(new Photo("demo/images/galleria/photo7.jpg", "demo/images/galleria/galleria5s.jpg",
                "Description for Image 5", "Title 5"));
       
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
