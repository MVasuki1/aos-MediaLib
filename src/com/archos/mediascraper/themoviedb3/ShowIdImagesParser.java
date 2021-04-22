// Copyright 2021 Courville Software
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediascraper.themoviedb3;

import android.content.Context;
import android.util.Pair;
import com.archos.mediascraper.ScraperImage;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResult;
import com.uwetrottmann.thetvdb.entities.SeriesImageQueryResultResponse;
import com.uwetrottmann.tmdb2.entities.Image;
import com.uwetrottmann.tmdb2.entities.Images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowIdImagesParser {

    private static final Logger log = LoggerFactory.getLogger(ShowIdImagesParser.class);

    // TODO MARC refactor like movie for posters/backdrops gen

    public static ShowIdImagesResult getResult(String showTitle, Images images, Context context) {

        ShowIdImagesResult result = new ShowIdImagesResult();

        // posters
        List<ScraperImage> posters = new ArrayList<>();
        List<Pair<Image, String>> tempPosters = new ArrayList<>();

        // backdrops
        List<ScraperImage> backdrops = new ArrayList<>();
        List<Pair<Image, String>> tempBackdrops = new ArrayList<>();

        if (images.posters != null)
            for (Image poster : images.posters)
                tempPosters.add(Pair.create(poster, poster.iso_639_1));

        if (images.backdrops != null)
            for (Image backdrop : images.backdrops)
                tempBackdrops.add(Pair.create(backdrop, backdrop.iso_639_1));

        Collections.sort(tempPosters, new Comparator<Pair<Image, String>>() {
            @Override
            public int compare(Pair<Image, String> b1, Pair<Image, String> b2) {
                return - Double.compare(b1.first.vote_average, b2.first.vote_average);
            }
        });

        Collections.sort(tempBackdrops, new Comparator<Pair<Image, String>>() {
            @Override
            public int compare(Pair<Image, String> b1, Pair<Image, String> b2) {
                return - Double.compare(b1.first.vote_average, b2.first.vote_average);
            }
        });

        for(Pair<Image, String> poster : tempPosters) {
            log.debug("getResult: generating ScraperImage for poster for " + showTitle + ", large=" + ScraperImage.TMPL + poster.first.file_path);
            ScraperImage image = new ScraperImage(ScraperImage.Type.SHOW_POSTER, showTitle);
            image.setLanguage(poster.second);
            image.setLargeUrl(ScraperImage.TMPL + poster.first.file_path);
            // TODO MARC do we remove thumbs?
            image.setThumbUrl(ScraperImage.TMPT + poster.first.file_path);
            image.generateFileNames(context);
            posters.add(image);
        }

        for(Pair<Image, String> backdrop : tempBackdrops) {
            log.debug("getResult: generating ScraperImage for backdrop for " + showTitle + ", large=" + ScraperImage.TMPL + backdrop.first.file_path);
            ScraperImage image = new ScraperImage(ScraperImage.Type.SHOW_BACKDROP, showTitle);
            image.setLanguage(backdrop.second);
            // TODO MARC there is no point in thumb backdrops!
            image.setLargeUrl(ScraperImage.TMBL + backdrop.first.file_path);
            //image.setThumbUrl(ScraperImage.TMBT + backdrop.first.file_path);
            image.generateFileNames(context);
            backdrops.add(image);
        }

        result.posters = posters;
        result.backdrops = backdrops;
        return result;
    }
}
