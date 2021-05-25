precision mediump float; // 数据精度
// 纹理坐标
varying vec2 v_TexCoord;
// 采样器
uniform sampler2D  u_TextureSampler;  // samplerExternalOES: 图片， 采样器

void main(){
    //  texture2D: vTexture采样器，采样  aCoord 这个像素点的RGBA值
    vec4 rgba = texture2D(u_TextureSampler,v_TexCoord);  //rgba

    // 灰色滤镜
//    float color=(rgba.r + rgba.g + rgba.b) / 3.0;
//    vec4 tempColor=vec4(color,color,color,1);

    // 忘记了
//    gl_FragColor = rgba+vec4(0.1,0.1,0.0,0.0);
//    gl_FragColor = rgba+vec4(0.0,0.0,0.3,0.0);

    //顺时针旋转180°
//    vec4 rgba = texture2D(vTexture, vec2(aCoord.y, aCoord.x));
    //顺时针旋转90度
    //    vec4 rgba = texture2D(vTexture, vec2(aCoord.y, 1.0-aCoord.x));

    gl_FragColor = rgba;
}