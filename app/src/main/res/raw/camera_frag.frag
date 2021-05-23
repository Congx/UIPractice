#extension GL_OES_EGL_image_external : require
precision mediump float;
//所有float类型数据的精度是lowp
varying vec2 v_TexCoord;
//uniform vec4 u_Color;
//采样器  uniform static
uniform samplerExternalOES u_TextureUnit;
void main(){
//Opengl 自带函数
    vec4 rgba = texture2D(u_TextureUnit,v_TexCoord);
    gl_FragColor=vec4(rgba.r,rgba.g,rgba.b,rgba.a);
//    gl_FragColor=u_Color;
}