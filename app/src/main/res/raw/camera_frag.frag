#extension GL_OES_EGL_image_external : require
//必须 写的 固定的  意思   用采样器
//所有float类型数据的精度是lowp
varying vec2 v_TexCoord;
//采样器  uniform static
uniform samplerExternalOES u_TextureUnit;
void main(){
//Opengl 自带函数
    vec4 rgba = texture2D(u_TextureUnit,v_TexCoord);
    gl_FragColor=vec4(rgba.r,rgba.g,rgba.b,rgba.a);
}