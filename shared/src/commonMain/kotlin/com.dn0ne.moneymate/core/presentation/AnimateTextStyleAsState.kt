package com.dn0ne.moneymate.core.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.lerp

@Composable
fun animateTextStyleAsState(
    targetValue: TextStyle,
    animationSpec: AnimationSpec<Float> = spring(),
    finishedListener: ((TextStyle) -> Unit)? = null  
): State<TextStyle> {
  
    val animation = remember { Animatable(0f) }
    var previousTextStyle by remember { mutableStateOf(targetValue) }
    var nextTextStyle by remember { mutableStateOf(targetValue) }  
  
    val textStyleState = remember {
        derivedStateOf {  
            lerp(previousTextStyle, nextTextStyle, animation.value)  
        }  
    }    
    
    LaunchedEffect(targetValue, animationSpec) {  
        previousTextStyle = textStyleState.value  
        nextTextStyle = targetValue 
        animation.snapTo(0f)  
        animation.animateTo(1f, animationSpec)  
        finishedListener?.invoke(textStyleState.value)  
    }  
  
    return textStyleState  
}