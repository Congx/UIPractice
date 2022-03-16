precision mediump float; // 数据精度
// 纹理坐标
varying vec2 v_TexCoord;
// 采样器
uniform sampler2D  u_TextureSampler;  // samplerExternalOES: 图片， 采样器

uniform float scalePercent;
uniform float mixturePercent;

void main(){
    vec2 center = vec2(0.5,0.5);
    vec2 resultCoord = (v_TexCoord - center) / scalePercent + center;
//    resultCoord = resultCoord / scalePercent;
//    resultCoord += center;

    vec4 rgba1 = texture2D(u_TextureSampler,v_TexCoord);  //rgba
    vec4 rgba2 = texture2D(u_TextureSampler,resultCoord);  //rgba

    gl_FragColor = mix(rgba1,rgba2,mixturePercent);
}