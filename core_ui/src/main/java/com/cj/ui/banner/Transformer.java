package com.cj.ui.banner;


import androidx.viewpager.widget.ViewPager.PageTransformer;

import com.cj.ui.banner.transformer.AccordionTransformer;
import com.cj.ui.banner.transformer.BackgroundToForegroundTransformer;
import com.cj.ui.banner.transformer.CubeInTransformer;
import com.cj.ui.banner.transformer.CubeOutTransformer;
import com.cj.ui.banner.transformer.DefaultTransformer;
import com.cj.ui.banner.transformer.DepthPageTransformer;
import com.cj.ui.banner.transformer.FlipHorizontalTransformer;
import com.cj.ui.banner.transformer.FlipVerticalTransformer;
import com.cj.ui.banner.transformer.ForegroundToBackgroundTransformer;
import com.cj.ui.banner.transformer.RotateDownTransformer;
import com.cj.ui.banner.transformer.RotateUpTransformer;
import com.cj.ui.banner.transformer.ScaleInOutTransformer;
import com.cj.ui.banner.transformer.StackTransformer;
import com.cj.ui.banner.transformer.TabletTransformer;
import com.cj.ui.banner.transformer.ZoomInTransformer;
import com.cj.ui.banner.transformer.ZoomOutSlideTransformer;
import com.cj.ui.banner.transformer.ZoomOutTranformer;

public class Transformer {

    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}
