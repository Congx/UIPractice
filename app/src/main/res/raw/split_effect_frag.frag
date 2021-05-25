precision mediump float; // 数据精度
// 纹理坐标
varying vec2 v_TexCoord;
// 采样器
uniform sampler2D  u_TextureSampler;  // samplerExternalOES: 图片， 采样器


void main(){
    float y = v_TexCoord.y;

    if(y < 0.5) {
        y += 0.25;
    }else {
        y -= 0.25;
    }
    gl_FragColor = texture2D(u_TextureSampler,vec2(v_TexCoord.x,y));  //rgba
}