precision mediump float; // 数据精度
// 纹理坐标
varying vec2 v_TexCoord;
// 采样器
uniform sampler2D  u_TextureSampler;  // samplerExternalOES: 图片， 采样器

void main(){
    //  texture2D: vTexture采样器，采样  aCoord 这个像素点的RGBA值
    vec4 rgba = texture2D(u_TextureSampler,v_TexCoord);  //rgba
    gl_FragColor = rgba;
}