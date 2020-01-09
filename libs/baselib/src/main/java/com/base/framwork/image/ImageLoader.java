package com.base.framwork.image;

/**
 * @date 2020-01-09
 * @Author luffy
 * @description
 */
public class ImageLoader{

        public static final String type_Glide="Glide";
        public static final String type_Picasso="Picasso";
        public static final String type_default =type_Glide;

        /**
         * glide全局默认占位图
         */
        public static int globeProgressId = -1;
        public static int globeErrorId = -1;

        private ImageLoader(){

        }

        public static IimageLoader getRequest(){
            return getRequest(type_default);

        }

        public static IimageLoader getRequest(String type){
            switch (type){
                default:
                    return new GlideLoader();
            }

        }
}
