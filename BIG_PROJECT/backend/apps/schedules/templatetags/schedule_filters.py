from django import template

register = template.Library()

@register.filter
def dict_get(dictionary, key):
    """Get value from dictionary by key, return empty string if not found"""
    if dictionary is None:
        return ''
    return dictionary.get(key, '')
